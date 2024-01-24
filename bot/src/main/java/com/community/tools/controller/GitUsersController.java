package com.community.tools.controller;

import static org.springframework.http.ResponseEntity.ok;

import com.community.tools.service.MessageConstructor;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.github.GitHubService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("app")
public class GitUsersController {

  private StateMachineService stateMachineService;
  private final GitHubService gitService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private MessageConstructor messageConstructor;

  /**
   * Endpoint /git. Method GET.
   *
   * @return ResponseEntity with Status.OK and List of all users in GH repository
   */
  @ApiOperation(value = "Returns list of github logins"
      + " of Broscorp-net/traineeship collaborators")
  @GetMapping(value = "/git", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getGitHubAllUsers() {
    Set<GHUser> gitHubAllUsers = gitService.getGitHubAllUsers();

    List<String> listGitUsersLogin = gitHubAllUsers.stream().map(GHPerson::getLogin)
        .collect(Collectors.toList());

    return ok().body(listGitUsersLogin);
  }

  // setter injection is used to avoid circular dependency
  // todo: figure out why StateMachineService is used here
  @Autowired
  public void setStateMachineService(StateMachineService stateMachineService) {
    this.stateMachineService = stateMachineService;
  }
}
