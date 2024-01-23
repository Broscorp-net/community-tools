package com.community.tools.service.github.event;

import com.community.tools.service.AbstractEventProcessingService;
import java.util.List;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GithubEventsProcessingService extends AbstractEventProcessingService<JSONObject> {

  public GithubEventsProcessingService(List<EventHandler<JSONObject>> eventHandlers) {
    super(eventHandlers);
  }
}
