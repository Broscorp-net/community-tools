package com.community.tools.service;

import com.community.tools.model.ServiceUser;
import com.community.tools.model.User;
import com.community.tools.service.github.GitHubService;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private GitHubService gitHubService;


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
  public String getLeaderboardTemplate()  {
    final Context ctx = new Context();
    List<User> list = getActiveUsersFromPeriod(180);
    list.sort(Comparator.comparing(User::getTotalPoints).reversed());
    List<User> listFirst = list.stream().limit(5).collect(Collectors.toList());
    ctx.setVariable("entities", listFirst);
    final String htmlContent = this.templateEngine.process("leaderboard.html", ctx);
    return  htmlContent;
  }

  /**
   * This method load users and add Name to the User model.
   * @return List of Users.
   */
  public List<User> addNameToUser() {
    List<User> list = stateMachineRepository.findAll();
    Set<ServiceUser> users = messageService.getAllUsers();
    Map<String, String> map = users.stream()
            .filter(u -> u.getName() != null)
            .collect(Collectors.toMap(ServiceUser::getId, ServiceUser::getName));
    for (User user: list) {
      String name = map.get(user.getUserID());
      user.setPlatformName(name);
    }
    return list;
  }

  /**
   * This method get active users from period in days
   * and add their to the User model.
   * @param days Period in days.
   * @return List of Users.
   */
  public  List<User> getActiveUsersFromPeriod(int days)  {
    LocalDate tempDate = LocalDate.now().minusDays(days);
    Date date = Date.from(tempDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    List<User> list = addNameToUser();
    Set<String> userNames = gitHubService.getActiveUsersFromGit(date);
    List<User> userList = list.stream()
            .filter(user -> userNames
                    .contains(user.getGitName())).collect(Collectors.toList());
    return userList;
  }
}
