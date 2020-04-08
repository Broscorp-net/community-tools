package com.community.tools.service.github;

import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

@Service
public class GitHubHookServlet extends HttpServlet {

  @Value("${spring.datasource.url}")
  private String url;
  @Value("${spring.datasource.username}")
  private String username;
  @Value("${spring.datasource.password}")
  private String password;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    StringBuilder builder = new StringBuilder();
    String aux = "";

    while ((aux = req.getReader().readLine()) != null) {
      builder.append(aux);
    }

    SingleConnectionDataSource connect = new SingleConnectionDataSource();
    connect.setUrl(url);
    connect.setUsername(username);
    connect.setPassword(password);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);
    JSONObject json = new JSONObject(builder.toString());
    jdbcTemplate.update(
        "INSERT INTO public.\"GitHookData\" (time, json_data) VALUES ('" + new Date() + "','" + json
            + "'::json);");
  }
}
