package com.community.tools.service.github;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class DiscordGithubService {

  public Map<String, String> getDiscordGithubUsernames(List<String> githubUsernames) {
    return Collections.emptyMap();
  }

  public String getDiscordName(String githubName) {
    return Strings.EMPTY;
  }
}

