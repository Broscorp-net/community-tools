package com.community.tools.config;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;



@Configuration
public class EmailConfig {
  @Value("${email.login}")
  private String email;
  @Value("${email.password}")
  private String password;

  /**
   * Config Gmail for send email.
   * @return JavaMailSender.
   **/
  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername(email);
    mailSender.setPassword(password);

    Properties prop = mailSender.getJavaMailProperties();
    prop.put("mail.transport.protocol", "smtp");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");
    prop.put("mail.debug", "true");

    return mailSender;
  }

  /**
   * Config receive mail.
   * @return Store.
   * @throws MessagingException
   **/
  @Bean
  public Store getJavaMailReceiver() throws MessagingException {
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
    Store store = session.getStore();
    store.connect("imap.gmail.com", email, password);
    return store;
  }

}
