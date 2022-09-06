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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
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
  private final RepositoryNameService repositoryNameService;

  @Autowired
  public ClassroomServiceImpl(GitHub gitHub,
      @Value("${github.organization.main}") String mainOrganizationName,
      @Value("${github.organization.traineeship}") String traineeshipOrganizationName,
      @Value("${github.team.trainees}") String traineesTeamName,
      RepositoryNameService repositoryNameService) {
    this.gitHub = gitHub;
    this.mainOrganizationName = mainOrganizationName;
    this.traineeshipOrganizationName = traineeshipOrganizationName;
    this.traineesTeamName = traineesTeamName;
    this.repositoryNameService = repositoryNameService;
  }

  @SneakyThrows
  @Override
  public void addUserToOrganization(String gitName) {
    GHUser user = gitHub.getUser(gitName);

    GHOrganization organization = gitHub.getMyOrganizations().get(mainOrganizationName);
    GHTeam traineesTeam = organization.getTeamByName(traineesTeamName);

    traineesTeam.add(user);
  }

  @SneakyThrows
  @Override
  public List<GithubUserDto> getAllActiveUsers(Period period) {
    Date startDate = Date.from(LocalDate
        .now()
        .minus(period)
        .atStartOfDay(ZoneId.systemDefault()).toInstant());

    GHOrganization organization = gitHub.getMyOrganizations().get(traineeshipOrganizationName);

    Map<String, List<GHRepository>> allUsersRepositories = organization
        .getRepositories()
        .entrySet()
        .stream()
        .filter(entry -> repositoryNameService.isPrefixedWithTaskName(entry.getKey()))
        .map(Entry::getValue)
        .collect(groupingBy(repository -> {
          ParsedRepositoryName parsedName = repositoryNameService.parseRepositoryName(
              repository.getName());
          return parsedName.getCreatorGitName();
        }));

    allUsersRepositories
        .entrySet()
        .removeIf(entry -> {
          List<GHRepository> userRepositories = entry.getValue();
          return userRepositories.stream().noneMatch(repository -> {
            try {
              return repository.getUpdatedAt().after(startDate);
            } catch (IOException exception) {
              throw new RuntimeException(exception);
            }
          });
        });

    return allUsersRepositories
        .entrySet()
        .stream()
        .map(entry -> buildGithubUserDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  private GithubUserDto buildGithubUserDto(String creatorGitName, List<GHRepository> repositories) {
    repositories.sort(comparing(repository -> {
      try {
        return repository.getUpdatedAt();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }));

    return GithubUserDto.builder()
        .gitName(creatorGitName)
        .lastCommit(getUpdatedAt(repositories.get(repositories.size() - 1)))
        .repositories(repositories
            .stream()
            .map(this::buildGithubRepositoryDto)
            .collect(Collectors.toList()))
        .build();
  }

  @SneakyThrows
  private GithubRepositoryDto buildGithubRepositoryDto(GHRepository repository) {
    String repositoryName = repository.getName();
    ParsedRepositoryName parsedRepositoryName = repositoryNameService.parseRepositoryName(
        repository.getName());
    GHWorkflowRun workflowRun = getWorkflowRun(repository);

    return GithubRepositoryDto.builder()
        .repositoryName(repositoryName)
        .taskName(parsedRepositoryName.getTaskName())
        .lastBuildStatus(getLastBuildStatus(workflowRun))
        .labels(getLabels(repository))
        .points(getPoints(workflowRun))
        .createdAt(getCreatedAt(repository))
        .updatedAt(getUpdatedAt(repository))
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
        .getWorkflow("classroom.yml")
        .listRuns()
        .withPageSize(1)
        .iterator()
        .next();
  }

  private Set<String> getLabels(GHRepository repository) {
    try {
      return repository
          .getPullRequest(1)
          .getLabels()
          .stream()
          .map(GHLabel::getName)
          .collect(Collectors.toSet());
    } catch (GHFileNotFoundException e) {
      return Collections.emptySet();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SneakyThrows
  private LocalDate getCreatedAt(GHRepository repository) {
    return repository
        .getCreatedAt()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  @SneakyThrows
  private LocalDate getUpdatedAt(GHRepository repository) {
    return repository
        .getUpdatedAt()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }
}

