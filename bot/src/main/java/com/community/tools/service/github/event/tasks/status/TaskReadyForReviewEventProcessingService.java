package com.community.tools.service.github.event.tasks.status;

import com.community.tools.dto.events.tasks.TaskReadyForReviewEventDto;
import com.community.tools.service.AbstractEventProcessingService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskReadyForReviewEventProcessingService extends
    AbstractEventProcessingService<TaskReadyForReviewEventDto> {

  public TaskReadyForReviewEventProcessingService(
      List<EventHandler<TaskReadyForReviewEventDto>> eventHandlers) {
    super(eventHandlers);
  }
}
