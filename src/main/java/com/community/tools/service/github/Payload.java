package com.community.tools.service.github;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Payload extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    StringBuilder builder = new StringBuilder();
    String aux = "";

    while ((aux = req.getReader().readLine()) != null) {
      builder.append(aux);
    }

    String text = builder.toString();
    try {
      JSONObject json = new JSONObject(text);

      String teams_url = json.getJSONObject("repository").getString("teams_url");
      System.out.println("Teams URL:: " + teams_url);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public ServletRegistrationBean<Payload> servletRegistrationBean() {
    return new ServletRegistrationBean<>(new Payload(), "/gitHook/*");
  }
}
