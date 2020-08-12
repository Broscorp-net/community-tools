package com.community.tools.util;

import com.community.tools.service.github.GitHubHookServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class ServletConfig {

  private final GitHubHookServlet gitHubHookServlet;

  @Bean
  public ServletRegistrationBean<GitHubHookServlet> servletRegistrationBeanGitHook() {
    return new ServletRegistrationBean<>(gitHubHookServlet, "/gitHook/*");
  }
}
