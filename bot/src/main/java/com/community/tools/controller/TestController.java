package com.community.tools.controller;

import com.community.tools.service.PublishWeekStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sendmessage")
public class TestController {

  @Autowired
  PublishWeekStatsService service;

  @GetMapping
  public void sendMessage(){
    service.publishTasksStatus();
  }

}
