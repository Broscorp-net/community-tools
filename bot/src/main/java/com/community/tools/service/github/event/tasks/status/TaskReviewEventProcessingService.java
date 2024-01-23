package com.community.tools.service.github.event.tasks.status;

import com.community.tools.dto.events.tasks.TaskReviewEventDto;
import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskReviewEventProcessingService extends
    AbstractEventProcessingService<TaskReviewEventDto> {

  public TaskReviewEventProcessingService(
      List<EventHandler<TaskReviewEventDto>> eventHandlers) {
    super(eventHandlers);
  }
}
