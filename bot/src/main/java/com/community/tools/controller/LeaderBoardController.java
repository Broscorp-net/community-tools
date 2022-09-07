package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForLeaderboardDto;
import com.community.tools.service.LeaderBoardService;
import java.time.Period;
import java.util.Comparator;
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
  @Value("${defaultUserLimit}")
  private Integer defaultUserLimit;
  private final LeaderBoardService leaderBoardService;

  public LeaderBoardController(LeaderBoardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<List<UserForLeaderboardDto>> getRepositories(
      @RequestParam(required = false) Optional<Integer> userLimit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> comparatorForSort) {
    Comparator<GithubUserDto> comparator = GithubUserDto.getComparatorForDescendingOrder();

    if (comparatorForSort.isPresent()) {

      if (comparatorForSort.get().equals("asc")) {
        comparator = GithubUserDto.getComparatorForAscendingOrder();
      } else if (comparatorForSort.get().equals("desc")) {
        comparator = GithubUserDto.getComparatorForDescendingOrder();
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

    }

    return new ResponseEntity<>(
        leaderBoardService.getLeaderBoard(
            userLimit.orElse(defaultUserLimit),
            Period.ofDays(days.orElse(defaultNumberOfDays)),
            comparator), HttpStatus.OK);
  }

}
