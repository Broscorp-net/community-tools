package com.community.tools.service.github;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.util.RepositoryNameService;
import com.community.tools.service.github.util.dto.ParsedRepositoryName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GHWorkflowRun;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ClassroomServiceImpl implements ClassroomService {

  private final GitHub gitHub;
  private final String mainOrganizationName;
  private final String traineeshipOrganizationName;
  private final String traineesTeamName;
  private final String classroomWorkflow;
  private final String completedTaskLabel;
  private final String defaultPullRequestName;
  private final RepositoryNameService repositoryNameService;

  @Autowired
  public ClassroomServiceImpl(GitHub gitHub,
      @Value("${github.organizations.main}") String mainOrganizationName,
      @Value("${github.organizations.traineeship}") String traineeshipOrganizationName,
      @Value("${github.teams.trainees}") String traineesTeamName,
      @Value("${github.workflows.classroom}") String classroomWorkflow,
      @Value("${github.labels.completed-task}") String completedTaskLabel,
      @Value("${github.pull-requests.default}") String defaultPullRequestName,
      RepositoryNameService repositoryNameService) {
    this.gitHub = gitHub;
    this.mainOrganizationName = mainOrganizationName;
    this.traineeshipOrganizationName = traineeshipOrganizationName;
    this.traineesTeamName = traineesTeamName;
    this.classroomWorkflow = classroomWorkflow;
    this.completedTaskLabel = completedTaskLabel;
    this.defaultPullRequestName = defaultPullRequestName;
    this.repositoryNameService = repositoryNameService;
  }

  /**
   * Adds user with passed GitHub login to the organization.
   *
   * @param gitName github login
   */
  @SneakyThrows
  @Override
  public void addUserToOrganization(String gitName) {
    GHUser user = gitHub.getUser(gitName);

    GHOrganization organization = gitHub.getMyOrganizations().get(mainOrganizationName);
    GHTeam traineesTeam = organization.getTeamByName(traineesTeamName);

    traineesTeam.add(user);
  }

  /**
   * Fetches users, who made at least one commit during the passed period and returns information
   * about them and their repositories.
   *
   * @param period period
   * @return information about users and their repositories
   */
  @SneakyThrows
  @Override
  public List<GithubUserDto> getAllActiveUsers(Period period) {
    GHOrganization organization = gitHub
        .getMyOrganizations()
        .get(traineeshipOrganizationName);

    Map<String, List<FetchedRepository>> allUserRepositories =
        fetchAllUserRepositories(organization);

    Date startDate = convertToDate(LocalDate
        .now()
        .minus(period));

    allUserRepositories
        .entrySet()
        .removeIf(entry -> {
          List<FetchedRepository> userRepositories = entry.getValue();
          return userRepositories.stream().noneMatch(fetchedRepository -> fetchedRepository
              .getLastCommitDate()
              .after(startDate)
          );
        });

    return allUserRepositories
        .entrySet()
        .stream()
        .map(entry -> buildGithubUserDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  private Map<String, List<FetchedRepository>> fetchAllUserRepositories(GHOrganization organization)
      throws IOException {
    return organization
        .getRepositories()
        .entrySet()
        .stream()
        .filter(entry -> repositoryNameService.isPrefixedWithTaskName(entry.getKey()))
        .map(entry -> {
          try {
            GHRepository repository = entry.getValue();
            Date lastCommitDate = repository
                .queryCommits()
                .pageSize(1)
                .list()
                .iterator()
                .next()
                .getCommitDate();

            return new FetchedRepository(repository, lastCommitDate);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(groupingBy(fetchedRepository -> {
          ParsedRepositoryName parsedName = repositoryNameService.parseRepositoryName(
              fetchedRepository.getRepository().getName());
          return parsedName.getCreatorGitName();
        }));
  }

  private GithubUserDto buildGithubUserDto(String creatorGitName,
      List<FetchedRepository> fetchedRepositories) {
    fetchedRepositories.sort(comparing(FetchedRepository::getLastCommitDate).reversed());

    LocalDate lastCommitDate = convertToLocalDate(fetchedRepositories.get(0).getLastCommitDate());

    List<GithubRepositoryDto> repositories = fetchedRepositories
        .stream()
        .map(this::buildGithubRepositoryDto)
        .collect(Collectors.toList());

    int totalPoints = getTotalPoints(repositories);
    int completedTasks = getCompletedTasks(repositories);

    return GithubUserDto.builder()
        .gitName(creatorGitName)
        .lastCommit(lastCommitDate)
        .repositories(repositories)
        .totalPoints(totalPoints)
        .completedTasks(completedTasks)
        .build();
  }

  private int getCompletedTasks(List<GithubRepositoryDto> repositories) {
    return (int) repositories
        .stream()
        .flatMap(repository -> repository.getLabels().stream())
        .map(String::toLowerCase)
        .filter(label -> label.equals(completedTaskLabel))
        .count();
  }

  private int getTotalPoints(List<GithubRepositoryDto> repositories) {
    return repositories
        .stream()
        .map(GithubRepositoryDto::getPoints)
        .reduce(0, Integer::sum);
  }

  @SneakyThrows
  private GithubRepositoryDto buildGithubRepositoryDto(FetchedRepository fetchedRepository) {
    GHRepository repository = fetchedRepository.getRepository();

    String repositoryName = repository.getName();

    ParsedRepositoryName parsedRepositoryName =
        repositoryNameService.parseRepositoryName(repository.getName());

    GHWorkflowRun workflowRun = getWorkflowRun(repository);

    return GithubRepositoryDto.builder()
        .repositoryName(repositoryName)
        .taskName(parsedRepositoryName.getTaskName())
        .lastBuildStatus(getLastBuildStatus(workflowRun))
        .labels(getLabels(repository))
        .points(getPoints(workflowRun))
        .createdAt(convertToLocalDate(repository.getCreatedAt()))
        .updatedAt(convertToLocalDate(fetchedRepository.getLastCommitDate()))
        .build();
  }

  @SneakyThrows
  private String getLastBuildStatus(GHWorkflowRun workflowRun) {
    return workflowRun
        .getConclusion()
        .toString();
  }

  @SneakyThrows
  private int getPoints(GHWorkflowRun workflowRun) {
    return workflowRun
        .listJobs()
        .toList()
        .get(0)
        .downloadLogs(in -> new BufferedReader(new InputStreamReader(in)))
        .lines()
        .filter(line -> line.contains("Points"))
        .map(line -> {
          String[] lineSplit = line.split(" ");
          String[] points = lineSplit[2].split("/");
          return Integer.parseInt(points[0]);
        })
        .findFirst()
        .orElse(0);
  }

  @SneakyThrows
  private GHWorkflowRun getWorkflowRun(GHRepository repository) {
    return repository
        .getWorkflow(classroomWorkflow)
        .listRuns()
        .withPageSize(1)
        .iterator()
        .next();
  }

  private List<String> getLabels(GHRepository repository) {
    try {
      List<GHPullRequest> openPullRequests = repository.getPullRequests(GHIssueState.OPEN);
      for (GHPullRequest pullRequest : openPullRequests) {
        if (pullRequest.getTitle().equals(defaultPullRequestName)) {
          return pullRequest.getLabels()
              .stream()
              .map(GHLabel::getName)
              .collect(Collectors.toList());
        }
      }

      return Collections.emptyList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Date convertToDate(LocalDate date) {
    return Date
        .from(date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());
  }

  private LocalDate convertToLocalDate(Date date) {
    return date
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  @Getter
  @AllArgsConstructor
  private static class FetchedRepository {

    private final GHRepository repository;
    private final Date lastCommitDate;
  }
}

