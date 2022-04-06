package com.community.tools.controller;

import com.community.tools.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailRestController {

  @Autowired
  EmailService email;

  @GetMapping
  public String adressMail() {  return "<form action = \"/email\" method = \"post\">" +
          " <input name = \"email\" type=\"text\" placeholder = \"input mail\">" +
          " <input  type = \"submit\" value = \"Send\">" +
          "</form>";  }

  @PostMapping
  public String sendMail(@RequestParam(name = "email") String emailSend) {  return
          "<form action = \"/email\" method = \"get\">" +
          " <label>" + email.sendEmail(emailSend) + " </label>" +
          " <input  type = \"submit\" value = \"OK\">" +
          "</form>";  }
}