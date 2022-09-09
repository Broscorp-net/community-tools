package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForLeaderboardDto;
import com.community.tools.service.LeaderboardService;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {

  @Value("${defaultNumberOfDaysForStatistic}")
  private Integer defaultNumberOfDays;
  @Value("${defaultRowLimit}")
  private Integer defaultUserLimit;
  private final LeaderboardService leaderBoardService;

  public LeaderboardController(LeaderboardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @GetMapping("/leaderboard")
  @CrossOrigin(origins = "http://localhost:4200")
  public ResponseEntity<List<UserForLeaderboardDto>> getRepositories(
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> sort) {
    Comparator<GithubUserDto> comparator = GithubUserDto.getComparatorForLeaderboardDESC();

    if (sort.isPresent()) {

      if (sort.get().equals("asc")) {
        comparator = GithubUserDto.getComparatorForLeaderboardASC();
      } else if (sort.get().equals("desc")) {
        comparator = GithubUserDto.getComparatorForLeaderboardDESC();
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

    }

    return new ResponseEntity<>(
        leaderBoardService.getLeaderBoard(
            limit.orElse(defaultUserLimit),
            Period.ofDays(days.orElse(defaultNumberOfDays)),
            comparator), HttpStatus.OK);
  }

}
