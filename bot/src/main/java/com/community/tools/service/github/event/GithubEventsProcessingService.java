package com.community.tools.service.github.event;

import java.util.List;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GithubEventsProcessingService {

  private final List<GithubEventHandler> eventHandlers;

  public void processEvent(JSONObject eventJson) {
    eventHandlers.forEach(eventHandler -> eventHandler.handleEvent(eventJson));
  }
}
