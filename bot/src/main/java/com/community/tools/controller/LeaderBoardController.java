package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.service.LeaderBoardService;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderBoardController {

  @Value("${defaultNumberOfDaysForStatistic}")
  private Integer defaultNumberOfDays;
  private final LeaderBoardService leaderBoardService;

  public LeaderBoardController(LeaderBoardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<List<GithubUserDto>> getRepositories(
      @RequestParam(required = false) Optional<Integer> days) {
    return new ResponseEntity<>(
        leaderBoardService.getActiveUsersFromPeriod(Period.ofDays(days.orElse(defaultNumberOfDays))),
        HttpStatus.OK);
  }

  //todo delete it later
  @GetMapping("/test/leaderboard")
  public ResponseEntity<String> testOfEndpoint(
      @RequestParam(required = false) Optional<Integer> days) {
    return new ResponseEntity<>(
        Period.ofDays(days.orElse(defaultNumberOfDays)).toString(),
        HttpStatus.OK);
  }

  @GetMapping("/test/getAllUsers")
  public ResponseEntity<List<GithubUserDto>> test() {
    return new ResponseEntity<>(leaderBoardService.test(Period.ofDays(20)),
        HttpStatus.OK);
  }

}
