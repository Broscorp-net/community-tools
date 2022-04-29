package com.community.tools.service;

import com.community.tools.model.Messages;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
  @Autowired
  public JavaMailSender emailSender;

  /**
   * This method send email.
   * @param userEmail email recipient.
  */
  public void sendEmail(String userEmail) {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = null;
    try {

      helper = new MimeMessageHelper(message, true, "utf-8");
      helper.setTo(userEmail);
      helper.setSubject("Bro_Bot Email");
      helper.setText(Messages.EMAIL, true);

    } catch (MessagingException e) {
      log.info("Can't send Email. " + e.getMessage());
    }

    // Send Email!
    this.emailSender.send(message);
  }
}
