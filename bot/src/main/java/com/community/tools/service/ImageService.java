package com.community.tools.service;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.JEditorPane;
import lombok.SneakyThrows;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *doesn't work
 * refactor it or delete
 */
@Deprecated
public class ImageService {
  /**
   * This method put html code into JEditorPane and print image.
   *
   * @param html template of webpage to form image from
   * @return byte array with image
   */
  @SneakyThrows
  public byte[] createImage(String html) {
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

    return bos.toByteArray();
  }

  /**
   * Get current base url.
   *
   * @return base url
   */
  public String getCurrentBaseUrl() {
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    HttpServletRequest req = sra.getRequest();
    return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
        + req.getContextPath();
  }

  public String getLeaderboardTemplate() {
    return new RestTemplate().getForObject(
        getCurrentBaseUrl() + "/task-status?userLimit=5", String.class);
  }

  public String getTaskStatusTemplate() {
    return new RestTemplate().getForObject(
        getCurrentBaseUrl() + "/leaderboard?userLimit=5&daysFetch=180sort=points", String.class);
  }

}
