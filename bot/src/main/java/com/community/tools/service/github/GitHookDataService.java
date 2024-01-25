package com.community.tools.service.github;

import java.sql.SQLException;
import java.util.Date;
import javax.sql.DataSource;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GitHookDataService {

  private final String sql = "INSERT INTO public.\"GitHookData\" (time, jsonb_data) VALUES (? , ?)";

  private JdbcTemplate connection;

  @Autowired
  public void initConnection(DataSource dataSource) {
    this.connection = new JdbcTemplate(dataSource);
  }

  /**
   * Save JSON from GitHub to GitHookData table into Database.
   *
   * @param json Json from GitHub
   */
  public void saveDataIntoDb(JSONObject json) {
    Date date = new Date();
    PGobject out = new PGobject();
    out.setType("json");
    try {
      out.setValue(json.toString());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    connection.update(sql, date, out);
  }
}
