package com.community.tools.controller;

import com.community.tools.dto.UserDto;
import com.community.tools.service.LeaderBoardService;
import java.time.Period;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//TODO change restController to controller and add front
@RestController
public class LeaderBoardController {

  private final LeaderBoardService leaderBoardService;

  public LeaderBoardController(LeaderBoardService leaderBoardService) {
    this.leaderBoardService = leaderBoardService;
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<List<UserDto>> getLeaderboard() {
    return new ResponseEntity<>(leaderBoardService.getActiveUsersFromPeriod(Period.ofDays(7)),
        HttpStatus.OK);
  }

}
