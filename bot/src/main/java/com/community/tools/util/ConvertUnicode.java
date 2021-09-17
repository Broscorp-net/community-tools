package com.community.tools.util;

import com.mgnt.utils.StringUnicodeEncoderDecoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConvertUnicode {

  /**
   * This method convert Cyrillic characters from file to Unicode.
   * @param fileName name of text file
   * @return HashMap with key = property name and value - text property
   * @throws IOException IOException
   */
  public HashMap<String,String> convertToUnicode(String fileName) throws IOException {
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



}
