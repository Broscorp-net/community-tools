package com.community.tools.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class SwaggerConfig {

  private final Contact contact = new Contact(
          "Broscorp",
          "https://broscorp.net/",
          "contact@broscorp.net");

  private final ApiInfo apiInfo = new ApiInfo(
          "REST API for Brobot",
          "This page documents REST Brobot endpoints",
          "@Brobot",
          "1.0",
          contact,
          "Apache 2.0",
          "@Brobot",
          new ArrayList<>());

  /**
   * Describes Docket object via builder.
   *
   * @return docket
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPS")))
            .apiInfo(apiInfo)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.community.tools"))
            .paths(PathSelectors.any())
            .build();
  }
}
