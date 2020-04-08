package com.community.tools.service.github;

import com.community.tools.util.GithubAuthChecker;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
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
  @Value("${SECRET_TOKEN}")
  private String secret;

  @SneakyThrows
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

    StringBuilder builder = new StringBuilder();
    String aux = "";

    while ((aux = req.getReader().readLine()) != null) {
      builder.append(aux);
    }
    JSONObject json = new JSONObject(builder.toString());

    if (new GithubAuthChecker(secret)
        .checkSignature(req.getHeader("X-Hub-Signature"), builder.toString())) {

      SingleConnectionDataSource connect = new SingleConnectionDataSource();
      connect.setUrl(url);
      connect.setUsername(username);
      connect.setPassword(password);
      JdbcTemplate jdbcTemplate = new JdbcTemplate(connect);

      jdbcTemplate.update(
          "INSERT INTO public.\"GitHookData7\" (time, jsonb_data) VALUES ('" + new Date() + "','"
              + json + "'::jsonb);");
    }
  }
}
