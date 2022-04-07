package com.community.tools.controller;

import com.community.tools.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/email")
public class EmailRestController {

  @Autowired
  EmailService email;

  /**
   * Endpoint for input email.
   * @return String html.
   **/
  @GetMapping
  public String adressMail() {
    return "<form action = \"/email\" method = \"post\">"
            + " <input name = \"email\" type=\"text\" placeholder = \"input mail\">"
            + " <input  type = \"submit\" value = \"Send\">"
            + "</form>";
  }

  /**
   * Method send email, and answer to success.
   * @param emailSend address recipient.
   * @return String html to success.
   **/
  @PostMapping
  public String sendMail(@RequestParam(name = "email") String emailSend) {
    return
          "<form action = \"/email\" method = \"get\">"
                  + " <label>" + email.sendEmail(emailSend) + " </label>"
                  + " <input  type = \"submit\" value = \"OK\">"
                  + "</form>";
  }

  /**
   * Metod received email.
   * @return String html to success.
   **/
  @GetMapping("/receive")
  public String reciveMail() {
    email.getEmail();
    return "Mail received";
  }
}