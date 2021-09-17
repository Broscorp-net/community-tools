package com.community.tools.controller;

import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.model.User;
import com.community.tools.service.LeaderBoardService;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/leaderboard")
public class LeaderBoardController {

  @Autowired
  StateMachineRepository stateMachineRepository;

  @Autowired
  LeaderBoardService leaderBoardService;


  /**
   * This method return webpage with table of trainee's rating.
   *
   * @param model Model
   * @return webpage with template "leaderboard"
   */
  @GetMapping("/")
  public String getLeaderboard(Model model) {
    List<User> list = leaderBoardService.getActiveUsersFromPeriod(180);
    list.sort(Comparator.comparing(User::getTotalPoints).reversed());
    model.addAttribute("entities", list);

    List<User> fullList = leaderBoardService.addNameToUser();
    fullList.sort(Comparator.comparing(User::getTotalPoints).reversed());
    model.addAttribute("entitiesFull", fullList);
    return "leaderboard";
  }

  /**
   * This method return image with table, which contains first 5 trainees of leaderboard.
   *
   * @param response HttpServletResponse
   * @throws EntityNotFoundException EntityNotFoundException
   * @throws IOException             IOException
   */
  @RequestMapping(value = "/img/{date}", method = RequestMethod.GET)
  public void getImage(HttpServletResponse response) throws EntityNotFoundException, IOException {
    String url = getAddress();
    byte[] data = leaderBoardService.createImage(url);
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.getOutputStream().write(data);
    response.setContentLength(data.length);
  }


}
