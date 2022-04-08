package com.community.tools.service;

import com.community.tools.model.Messages;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  public JavaMailSender emailSender;
  @Autowired
  public Store store;

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

  /**
  * Method get email.
  */
  public void getEmail() {
    try {
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_WRITE);

      Message message = inbox.getMessage(inbox.getMessageCount());
      System.out.println(message.getSubject());
      message.setFlag(Flag.SEEN, true);

    } catch (NoSuchProviderException e) {
      System.err.println(e.getMessage());
    } catch (MessagingException e) {
      System.err.println(e.getMessage());
    }
  }


}
