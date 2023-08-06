package com.community.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.community.tools.util.IoUtils;
import com.mgnt.utils.StringUnicodeEncoderDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConvertIntoUnicodeTest {
  List<String> dataList = new ArrayList<>();
  List<String> expectedList = new ArrayList<>();
  HashMap<String,String> property = new HashMap<>();


  /**
   * This metod read test Data from files.
   * @throws IOException IOException
   * @throws URISyntaxException URISyntaxException
   */
  @BeforeAll
  public void readTextFromFile() throws IOException, URISyntaxException {
    Path path = Paths.get("src/test/resources/test.txt");
    Stream<String> lines = Files.lines(path);
    dataList = lines.collect(Collectors.toList());
    lines.close();

    Path pathExp = Paths.get("src/test/resources/expectedFile.txt");
    Stream<String> linesExp = Files.lines(pathExp);
    expectedList = linesExp.collect(Collectors.toList());
    linesExp.close();

    Path pathProp = Paths.get("src/test/resources/prop.txt");
    Stream<String> linesProp = Files.lines(pathProp);
    List<String> propList = linesProp.collect(Collectors.toList());
    for (String str : propList) {
      String[] arr = str.split(" = ");
      property.put(arr[0], arr[1]);
    }
    linesProp.close();

  }

  @Test
  public void convertIntoUnicodeAllText() {
    String str = property.get("str");
    String expectedResult = property.get("expectedResult");
    String result = StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(str);
    assertEquals(expectedResult, result);
  }

  @Test
  public void convertTextToUnicode() {
    String strWithLatin = property.get("strWithLatin");
    String expectedResultWithLatin = property.get("expectedResultWithLatin");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strWithLatin.length(); i++) {
      Character ch = strWithLatin.charAt(i);
      if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CYRILLIC)) {
        sb.append(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(ch.toString()));
      } else {
        sb.append(ch);
      }
    }
    System.out.println(sb);
    assertEquals(expectedResultWithLatin, sb.toString());
  }

  @Test
  public void convertTextFromFile() {
    StringBuilder sb = new StringBuilder();
    String expected = expectedList.get(0);
    for (String str : dataList) {
      for (int i = 0; i < str.length(); i++) {
        Character ch = str.charAt(i);
        if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CYRILLIC)) {
          sb.append(StringUnicodeEncoderDecoder.encodeStringToUnicodeSequence(ch.toString()));
        } else {
          sb.append(ch);
        }
      }
    }
    System.out.println(sb);
    assertEquals(expected,sb.toString());
  }

  @SneakyThrows
  @Test
  public void convertProperty() {
    Map<String,String> prop = IoUtils.convertToUnicode("src/test/resources/prop.txt");
    assertEquals(prop.get("firstValue"), "My first value");
  }

  @Test
  public void readFromFile() {
    Stream<String> stream = new BufferedReader(
        new InputStreamReader(ClassLoader.getSystemResourceAsStream("property.txt"))).lines();
    List<String> propList = stream.collect(Collectors.toList());
    for (String str : propList) {
      String[] arr = str.split(" = ");
      System.out.println(arr[0] + " " + arr[1]);
    }
    stream.close();
  }

}

