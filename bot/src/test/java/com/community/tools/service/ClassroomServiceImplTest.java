package com.community.tools.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.community.tools.dto.UserDto;
import com.community.tools.service.github.ClassroomService;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassroomServiceImplTest {

  @MockBean
  private GitHub gitHub;

  @Autowired
  private ClassroomService classroomService;

  @Value("${github.organization.main}")
  private String mainOrganizationName;

  @Value("${github.organization.traineeship}")
  private String traineeshipOrganizationName;

  @Value("${github.team.trainees}")
  private String traineesTeamName;

  @SneakyThrows
  @Test
  public void testAddUserToOrganization() {
    UserDto userDto = new UserDto("123", "test");

    GHUser ghUserMock = mock(GHUser.class);
    when(gitHub.getUser(userDto.getGitName())).thenReturn(ghUserMock);

    GHOrganization ghOrganizationMock = mock(GHOrganization.class);
    GHTeam ghTeamMock = mock(GHTeam.class);
    when(ghOrganizationMock.getTeamByName(traineesTeamName)).thenReturn(ghTeamMock);
    Map<String, GHOrganization> organizations = new HashMap<>();
    organizations.put(mainOrganizationName, ghOrganizationMock);
    when(gitHub.getMyOrganizations()).thenReturn(organizations);

    classroomService.addUserToOrganization(userDto);

    verify(ghTeamMock).add(ghUserMock);
  }

  @SneakyThrows
  @Test
  public void testGetAllActiveUsers() {
    GHRepository firstRepositoryMock = mock(GHRepository.class);
    when(firstRepositoryMock.getOwnerName()).thenReturn("TestUser1");
    when(firstRepositoryMock.getUpdatedAt()).thenReturn(
        Date.from(LocalDateTime.now().minusDays(30).atZone(ZoneId.systemDefault()).toInstant()));

    GHRepository secondRepositoryMock = mock(GHRepository.class);
    when(secondRepositoryMock.getOwnerName()).thenReturn("TestUser1");
    when(secondRepositoryMock.getUpdatedAt()).thenReturn(
        Date.from(LocalDateTime.now().minusDays(22).atZone(ZoneId.systemDefault()).toInstant()));

    GHRepository thirdRepositoryMock = mock(GHRepository.class);
    when(thirdRepositoryMock.getOwnerName()).thenReturn("TestUser2");
    when(thirdRepositoryMock.getUpdatedAt()).thenReturn(
        Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));

    GHRepository fourthRepositoryMock = mock(GHRepository.class);
    when(fourthRepositoryMock.getOwnerName()).thenReturn("TestUser2");
    when(fourthRepositoryMock.getUpdatedAt()).thenReturn(
        Date.from(LocalDateTime.now().minusDays(45).atZone(ZoneId.systemDefault()).toInstant()));

    GHRepository fifthRepositoryMock = mock(GHRepository.class);
    when(fifthRepositoryMock.getOwnerName()).thenReturn("TestUser3");
    when(fifthRepositoryMock.getUpdatedAt()).thenReturn(
        Date.from(LocalDateTime.now().minusDays(5).atZone(ZoneId.systemDefault()).toInstant()));

    Map<String, GHRepository> repositoriesMocks = new HashMap<>();
    repositoriesMocks.put("intro-TestUser1", firstRepositoryMock);
    repositoriesMocks.put("generics-TestUser1", secondRepositoryMock);
    repositoriesMocks.put("game-of-life-TestUser2", thirdRepositoryMock);
    repositoriesMocks.put("gc-implementation-TestUser2", fourthRepositoryMock);
    repositoriesMocks.put("some-random-repo", fifthRepositoryMock);

    GHOrganization ghOrganizationMock = mock(GHOrganization.class);
    when(ghOrganizationMock.getRepositories()).thenReturn(repositoriesMocks);

    Map<String, GHOrganization> organizations = new HashMap<>();
    organizations.put(traineeshipOrganizationName, ghOrganizationMock);
    when(gitHub.getMyOrganizations()).thenReturn(organizations);

    List<UserDto> expectedUsers = Arrays.asList(
        new UserDto(null, "TestUser1"),
        new UserDto(null, "TestUser2")
    );

    List<UserDto> actualUsers = classroomService.getAllActiveUsers(Period.ofDays(35));

    assertEquals(2, actualUsers.size());
    assertTrue(actualUsers.contains(expectedUsers.get(0)));
    assertTrue(actualUsers.contains(expectedUsers.get(1)));
  }
}
