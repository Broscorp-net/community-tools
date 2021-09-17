package com.community.tools.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadPropertyFromFile {


  /**
   * This method reads text in the file and finds properties that are separated by "=".
   * @param fileName file, which contains properties
   * @return HashMap  Key(String) - name of property, Value(String) - value of property
   */
  public static HashMap<String, String> readPropertiesFromFile(String fileName) {
    HashMap<String,String> property = new HashMap<>();
    Stream<String> linesProp = new BufferedReader(
        new InputStreamReader(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName))).lines();
    List<String> propList = linesProp.collect(Collectors.toList());
    for (String str : propList) {
      String[] arr = str.split(" = ");
      property.put(arr[0], arr[1]);
    }
    linesProp.close();
    return property;
  }
}
