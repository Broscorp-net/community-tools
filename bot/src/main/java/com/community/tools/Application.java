package com.community.tools;

import com.community.tools.service.github.event.GithubEventHandler;
import com.community.tools.service.github.event.stats.GithubWorkflowRunEventHandler;
import com.community.tools.util.IoUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Application {

  /**
   * Build Spring Application with additional properties. Spring application uses
   * application.properties and Properties from props().
   *
   * @param args args
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder(Application.class)
        .properties(props())
        .build()
        .run(args);
  }

  /**
   * Add some property from file. Cyrillic text convert to Unicode. As example, file
   * "property.txt".
   *
   * @return Properties
   */
  private static Properties props() {
    Map<String, String> prop = IoUtils.readPropertiesFromFile("property.txt");
    Properties properties = new Properties();
    properties.putAll(prop);
    return properties;
  }

  @Bean("eventHandlers")
  @Profile("discord")
  private static List<GithubEventHandler> githubHookEventHandlers() {
    List<GithubEventHandler> githubEventHandlers = new ArrayList<>();
    githubEventHandlers.add(new GithubWorkflowRunEventHandler());
    return githubEventHandlers;
  }
}
