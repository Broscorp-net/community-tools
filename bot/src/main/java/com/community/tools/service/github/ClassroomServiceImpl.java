package com.community.tools.service.github;

import com.community.tools.dto.UserDto;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
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
  private final Set<String> taskRepositoryNamesPrefixes;

  @Autowired
  public ClassroomServiceImpl(GitHub gitHub,
      @Value("${github.organization.main}") String mainOrganizationName,
      @Value("${github.organization.traineeship}") String traineeshipOrganizationName,
      @Value("${github.team.trainees}") String traineesTeamName,
      @Value("${github.task-repository-names.prefixes}") String[] taskRepositoryNamesPrefixes) {
    this.gitHub = gitHub;
    this.mainOrganizationName = mainOrganizationName;
    this.traineeshipOrganizationName = traineeshipOrganizationName;
    this.traineesTeamName = traineesTeamName;
    this.taskRepositoryNamesPrefixes = new HashSet<>(Arrays.asList(taskRepositoryNamesPrefixes));
  }

  @SneakyThrows
  @Override
  public void addUserToOrganization(UserDto userDto) {
    String gitName = userDto.getGitName();
    GHUser user = gitHub.getUser(gitName);

    GHOrganization organization = gitHub.getMyOrganizations().get(mainOrganizationName);
    GHTeam traineesTeam = organization.getTeamByName(traineesTeamName);

    traineesTeam.add(user);
  }

  @SneakyThrows
  @Override
  public List<UserDto> getAllActiveUsers(Period period) {
    Date startDate = Date.from(LocalDate
        .now()
        .minus(period)
        .atStartOfDay(ZoneId.systemDefault()).toInstant());

    GHOrganization organization = gitHub.getMyOrganizations().get(traineeshipOrganizationName);
    List<GHRepository> activeRepositories = organization
        .getRepositories()
        .entrySet()
        .stream()
        .filter(entry -> {
          String repositoryName = entry.getKey();
          return taskRepositoryNamesPrefixes
              .stream()
              .anyMatch(repositoryName::startsWith);
        })
        .map(Entry::getValue)
        .filter(repository -> {
          try {
            return repository.getUpdatedAt().after(startDate);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());

    return activeRepositories
        .stream()
        .map(GHRepository::getOwnerName)
        .collect(Collectors.toSet())
        .stream()
        .map(ownerName -> new UserDto(null, ownerName))
        .collect(Collectors.toList());
  }
}
