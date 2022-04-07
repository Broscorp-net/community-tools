package com.community.tools.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class EmailServiceTest {

  @Autowired
  EmailService email;

  @Test
  public void sendMail() {
   // Assert.assertEquals(email.sendEmail("Shurick2211@gmail.com"),"Email Sent!");
  }
}
