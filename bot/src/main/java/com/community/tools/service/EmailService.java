package com.community.tools.service;

import com.community.tools.model.Messages;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
  @Autowired
  public JavaMailSender emailSender;

  /**
   * This method send email.
   * @param userEmail email recipient.
   * @return String of successfully sent.
   */
  public String sendEmail(String userEmail) {

    MimeMessage message = emailSender.createMimeMessage();

    MimeMessageHelper helper = null;
    try {

      helper = new MimeMessageHelper(message, true, "utf-8");
      helper.setTo(userEmail);
      helper.setSubject("Bro_Bot Email");
      helper.setText(Messages.EMAIL, true);

    } catch (MessagingException e) {
      e.printStackTrace();
    }

    // Send Email!
    this.emailSender.send(message);

    return "Email Sent!";
  }


}
