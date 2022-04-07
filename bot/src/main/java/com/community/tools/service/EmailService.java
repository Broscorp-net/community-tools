package com.community.tools.service;

import com.community.tools.model.Messages;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${email.login}")
  private String email;
  @Value("${email.password}")
  private String password;

  /**
   * Method get email.
   */
  public void getEmail() {
    Properties properties = new Properties();
    properties.put("mail.debug", "false");
    properties.put("mail.store.protocol", "imaps");
    properties.put("mail.imap.ssl.enable", "true");
    properties.put("mail.imap.port", "993");

    Authenticator auth = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(email,password);
      }
    };
    Session session = Session.getDefaultInstance(properties, auth);
    session.setDebug(false);
    try {
      Store store = session.getStore();
      store.connect("imap.gmail.com", email, password);
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_ONLY);
      //System.out.println("Количество сообщений : " + String.valueOf(inbox.getMessageCount()));
      //if (inbox.getMessageCount() == 0) return;
      Message message = inbox.getMessage(inbox.getMessageCount());
      System.out.println(message.getSubject());
      //sendEmail(message.getSubject());
      /*
        Multipart mp = (Multipart) message.getContent();
        // Вывод содержимого в консоль
        for (int i = 0; i < mp.getCount(); i++){
          BodyPart  bp = mp.getBodyPart(i);
          if (bp.getFileName() == null)
            System.out.println("    " + i + ". сообщение : '" +
                    bp.getContent() + "'");
          else
            System.out.println("    " + i + ". файл : '" +
                    bp.getFileName() + "'");
        }
      */
    } catch (NoSuchProviderException e) {
      System.err.println(e.getMessage());
    } catch (MessagingException e) {
      System.err.println(e.getMessage());
    }
  }


}
