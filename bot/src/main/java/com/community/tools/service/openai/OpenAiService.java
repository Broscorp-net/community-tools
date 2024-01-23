package com.community.tools.service.openai;

import com.community.tools.dto.OpenAiRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAiService {
  @Value("${openai.url}")
  private String url;
  @Value("${openai.token}")
  private String apiKey;
  @Value("${openai.model}")
  private String model;
  @Value("${openai.temperature}")
  private double temperature;
  private final ObjectMapper objectMapper;

  public OpenAiService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * This is the main method in service which processes prompt.
   *
   * @param prompt users request to the openai api
   * @return ready-to-use string response from AI
   */
  public String processPrompt(String prompt) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(apiKey);
      headers.setContentType(MediaType.APPLICATION_JSON);

      OpenAiRequestDto requestDto = new OpenAiRequestDto(model, prompt, temperature);

      RequestEntity<OpenAiRequestDto> requestEntity =
              new RequestEntity<>(requestDto, headers, HttpMethod.POST, URI.create(url));

      ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

      String jsonResponse = responseEntity.getBody();
      String raw = extractMessageFromJsonResponse(jsonResponse);
      return getStringWithLineSeparators(raw);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to communicate with OpenAI service", e);
    }
  }

  /**
   * This private method formatting string form json-looking to normal.
   *
   * @param response json string
   * @return raw string, example: "This is example list//n1)...//n..."
   */
  private String extractMessageFromJsonResponse(String response) throws IOException {
    JsonNode jsonNode = objectMapper.readTree(response);
    return jsonNode
            .get("choices").get(0)
            .get("message").get("content").asText();
  }

  /**
   * This private method replaces //n to /n in string.
   *
   * @param raw raw string with //n
   * @return ready-to-use string
   */
  private String getStringWithLineSeparators(String raw) {
    final String regex = "\\\\n";
    final String newSeparator = System.lineSeparator();
    return raw.replaceAll(regex,newSeparator);
  }
}
