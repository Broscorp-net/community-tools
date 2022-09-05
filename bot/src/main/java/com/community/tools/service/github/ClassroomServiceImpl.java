package com.community.tools.service.github;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import com.community.tools.dto.GithubRepositoryDto;
import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.github.util.RepositoryNameService;
import com.community.tools.service.github.util.dto.ParsedRepositoryName;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
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
    Map<String, List<GHRepository>> activeUserRepositories = organization
        .getRepositories()
        .entrySet()
        .stream()
        .filter(entry -> repositoryNameService.isPrefixedWithTaskName(entry.getKey()))
        .map(Entry::getValue)
        .filter(repository -> {
          try {
            return repository.getUpdatedAt().after(startDate);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(groupingBy(repository -> {
          ParsedRepositoryName parsedName = repositoryNameService.parseRepositoryName(
              repository.getName());
          return parsedName.getCreatorGitName();
        }));

    return activeUserRepositories
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
        .lastCommit(getUpdatedAt(repositories.get(0)))
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

    return GithubRepositoryDto.builder()
        .repositoryName(repositoryName)
        .taskName(parsedRepositoryName.getTaskName())
        .lastBuildStatus(getLastBuildStatus(repository))
        .labels(getLabels(repository))
        .createdAt(getCreatedAt(repository))
        .updatedAt(getUpdatedAt(repository))
        .build();
  }

  @SneakyThrows
  private String getLastBuildStatus(GHRepository repository) {
    return repository
        .getWorkflow("classroom.yml")
        .listRuns()
        .toList()
        .get(0)
        .getConclusion()
        .toString();
  }

  @SneakyThrows
  private Set<String> getLabels(GHRepository repository) {
    return repository
        .getPullRequest(0)
        .getLabels()
        .stream()
        .map(GHLabel::getName)
        .collect(Collectors.toSet());
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

