package com.community.tools.service.github;

import com.community.tools.dto.UserDto;
import java.time.Period;
import java.util.List;
import lombok.SneakyThrows;
import org.kohsuke.github.GHOrganization;
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

  @Autowired
  public ClassroomServiceImpl(GitHub gitHub,
      @Value("${github.organization.main}") String mainOrganizationName,
      @Value("${github.organization.traineeship}") String traineeshipOrganizationName,
      @Value("${github.team.trainees}") String traineesTeamName) {
    this.gitHub = gitHub;
    this.mainOrganizationName = mainOrganizationName;
    this.traineeshipOrganizationName = traineeshipOrganizationName;
    this.traineesTeamName = traineesTeamName;
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

  @Override
  public List<UserDto> getAllActiveUsers(Period period) {
    return null;
  }
}
