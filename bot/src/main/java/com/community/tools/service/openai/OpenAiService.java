package com.community.tools.service.openai;

import com.community.tools.dto.OpenAiRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {
  @Value("${openai.url}")
  private String url;
  @Value("${openai.token}")
  private String apiKey;
  @Value("${openai.model}")
  private String model;
  private final ObjectMapper objectMapper;

  public OpenAiService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * This is the main method in service which processes prompt.
   * @param prompt users request to the openai api
   * @return ready-to-use string response from AI
   */
  public String processPrompt(String prompt) {
    try {
      HttpURLConnection connection = createConnection();
      sendRequest(connection, prompt);
      String jsonResponse = readResponse(connection);
      String raw =  extractMessageFromJsonResponse(jsonResponse);
      return getStringWithLineSeparators(raw);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to communicate with OpenAI service", e);
    }
  }

  /**
   * This private method creates connection to api.
   * @return HttpURLConnection object
   * @throws IOException IOException
   */
  private HttpURLConnection createConnection() throws IOException {
    URL url = new URL(this.url);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Authorization", "Bearer " + apiKey);
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);
    return connection;
  }

  /**
   * This private method sends request to openai.
   * @param connection HttpURLConnection object from createConnection()
   * @param prompt users request to openai
   * @throws IOException IOException
   */
  private void sendRequest(HttpURLConnection connection, String prompt) throws IOException {
    OpenAiRequestDto requestDto = new OpenAiRequestDto(model,
            List.of(new OpenAiRequestDto.Message("user", prompt)));

    try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
      String requestBody = objectMapper.writeValueAsString(requestDto);
      writer.write(requestBody);
      writer.flush();
    }
  }

  /**
   * This private method gets json response from api.
   * @param connection HttpURLConnection object from createConnection()
   * @return json-looking string
   * @throws IOException IOException
   */
  private String readResponse(HttpURLConnection connection) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        response.append(line);
      }
      return response.toString();
    }
  }

  /**
   * This private method formatting string form json-looking to normal.
   * @param response json string
   * @return raw string, example: "This is example list//n1)...//n..."
   * @throws IOException IOException
   */
  private String extractMessageFromJsonResponse(String response) throws IOException {
    JsonNode jsonNode = objectMapper.readTree(response);
    return jsonNode
            .get("choices").get(0)
            .get("message").get("content").asText();
  }

  /**
   * This private method replaces //n to /n in string.
   * @param raw raw string with //n
   * @return ready-to-use string
   */
  private String getStringWithLineSeparators(String raw) {
    final String regex = "\\\\n";
    final String newSeparator = System.lineSeparator();
    return raw.replaceAll(regex, newSeparator);
  }
}
