package com.community.tools.controller;

import com.community.tools.dto.GithubUserDto;
import com.community.tools.dto.UserForLeaderboardDto;
import com.community.tools.service.LeaderboardService;
import java.time.Period;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private final Map<String, Comparator<GithubUserDto>> comparators
      = new HashMap<>();
  private final LeaderboardService leaderBoardService;

  public LeaderboardController(LeaderboardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
    comparators.put("DESC",
        Comparator.comparingInt(GithubUserDto::getTotalPoints).reversed());
    comparators.put("ASC",
        Comparator.comparingInt(GithubUserDto::getTotalPoints));
  }

  /**
   * Endpoint for leaderboard service.
   * @param limit - limit of users for view.
   * @param days - period of days fow view.
   * @param sort - sort order (DESC, ASC).
   * @return - return list of DTO.
   */
  @GetMapping("/leaderboard")
  @CrossOrigin("http://localhost:4200")
  public ResponseEntity<List<UserForLeaderboardDto>> getRepositories(
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<Integer> days,
      @RequestParam(required = false) Optional<String> sort) {

    Comparator<GithubUserDto> comparator = comparators.getOrDefault(
        sort.orElse("DESC").toUpperCase(),
        Comparator.comparingInt(GithubUserDto::getCompletedTasks).reversed());

    return new ResponseEntity<>(
        leaderBoardService.getLeaderBoard(
            limit.orElse(defaultUserLimit),
            Period.ofDays(days.orElse(defaultNumberOfDays)),
            comparator), HttpStatus.OK);
  }

}
