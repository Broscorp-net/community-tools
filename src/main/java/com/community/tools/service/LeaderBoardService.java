package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class LeaderBoardService {

  @Autowired
  StateMachineRepository stateMachineRepository;

  @Autowired
  TemplateEngine templateEngine;

  @Autowired
  private MessageService messageService;


  /**
   * This method put html code into JEditorPane and print image.
   * @param url url with endpoint leaderboard
   * @return byte array with image
   */
  @SneakyThrows
  public byte[] createImage(String url) {
    String html = getLeaderboardTemplate();
    int width = 700;
    int height = 350;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = image.createGraphics();

    JEditorPane jep = new JEditorPane("text/html", html);
    jep.setSize(width, height);
    jep.setBackground(Color.WHITE);
    jep.print(graphics);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", bos);
    byte[] data = bos.toByteArray();
    return data;
  }

  /**
   * This method return html-content with table, which contains first 5 trainees of leaderboard.
   * @return HtmlContent with leaderboard image
   */
  public String getLeaderboardTemplate() {
    final Context ctx = new Context();
    List<User> list = addSlackNameToUser();
    list.sort(Comparator.comparing(User::getTotalPoints).reversed());
    List<User> listFirst = list.stream().limit(5).collect(Collectors.toList());
    ctx.setVariable("entities", listFirst);
    final String htmlContent = this.templateEngine.process("leaderboard.html", ctx);
    return  htmlContent;
  }

  /**
   * This method load slack users and add slackName to the User model.
   * @return List of Users.
   */
  public List<User> addSlackNameToUser() {
    List<User> list = stateMachineRepository.findAll();
    Set<com.github.seratch.jslack.api.model.User> slackUsers = messageService.getAllUsers();
    Map<String, String> map = slackUsers.stream()
            .filter(u -> u.getRealName() != null)
            .collect(Collectors.toMap(user -> user.getId(), user -> user.getRealName()));
    for (User user: list) {
      String slackName = map.get(user.getUserID());
      user.setSlackLogin(slackName);
    }
    return list;
  }

}
