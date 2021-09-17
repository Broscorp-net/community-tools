package com.community.tools;

import static com.community.tools.util.ReadPropertyFromFile.readPropertiesFromFile;

import java.util.HashMap;
import java.util.Properties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
    HashMap<String, String> prop = readPropertiesFromFile("property.txt");
    Properties properties = new Properties();
    properties.putAll(prop);
    return properties;
  }
}
