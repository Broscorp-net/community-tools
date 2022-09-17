package com.community.tools.util;

import com.mgnt.utils.StringUnicodeEncoderDecoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IoUtils {

  private IoUtils() {}

  //TODO rework it
  /**
   * Getting properties from file.
   * @param fileName - file name
   * @return - map with properties
   * @throws IOException - exception
   */
  public static Map<String,String> convertToUnicode(String fileName) throws IOException {
    Path path = Paths.get(fileName);
    Stream<String> lines = Files.lines(path);
    List<String> dataList = lines.collect(Collectors.toList());
    lines.close();
    HashMap<String,String> properties = new HashMap<>();

    for (String str : dataList) {
      String[] arr = str.split(" = ");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < arr[1].length(); i++) {
        Character ch = str.charAt(i);
        if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CYRILLIC)) {
          sb.append(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(ch.toString()));
        } else {
          sb.append(ch);
        }
      }
      properties.put(arr[0], arr[1]);
    }
    return properties;
  }

  /**
   * This method reads text in the file and finds properties that are separated by "=".
   * @param fileName file, which contains properties
   * @return HashMap  Key(String) - name of property, Value(String) - value of property
   */
  public static Map<String, String> readPropertiesFromFile(String fileName) {
    HashMap<String,String> property = new HashMap<>();
    Stream<String> linesProp = new BufferedReader(
        new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(fileName)))).lines();
    List<String> propList = linesProp.collect(Collectors.toList());
    for (String str : propList) {
      String[] arr = str.split(" = ");
      property.put(arr[0], arr[1]);
    }
    linesProp.close();
    return property;
  }

}
