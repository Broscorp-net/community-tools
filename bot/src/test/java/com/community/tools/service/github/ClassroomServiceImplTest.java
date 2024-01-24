package com.community.tools.service.github;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClassroomServiceImplTest.TestConfig.class)
public class ClassroomServiceImplTest {

  @MockBean
  private GitHub gitHub;

  @Autowired
  private ClassroomService classroomService;

  @Value("${github.main-organization-name}")
  private String mainOrganizationName;

  @Value("${github.teams.trainees}")
  private String traineesTeamName;

  @BeforeEach
  public void setup() {

  }

  @SneakyThrows
  @Test
  public void testAddUserToOrganization() {
    String userGitName = "test";

    GHUser ghUserMock = mock(GHUser.class);
    when(gitHub.getUser(userGitName)).thenReturn(ghUserMock);

    GHOrganization ghOrganizationMock = mock(GHOrganization.class);
    GHTeam ghTeamMock = mock(GHTeam.class);
    when(ghOrganizationMock.getTeamByName(traineesTeamName)).thenReturn(ghTeamMock);
    Map<String, GHOrganization> organizations = new HashMap<>();
    organizations.put(mainOrganizationName, ghOrganizationMock);
    when(gitHub.getMyOrganizations()).thenReturn(organizations);

    classroomService.addUserToTraineesTeam(userGitName);

    verify(ghTeamMock).add(ghUserMock);
  }

  @Configuration
  static class TestConfig {
  }
}
