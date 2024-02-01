package com.community.tools.service.github.event;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.service.github.event.listeners.TaskStatusChangeEventListener;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskStatusChangeEventDispatcher {

  private final List<TaskStatusChangeEventListener> taskStatusChangeEventListenerList;

  /**
   * Passes a TaskStatusChangeEventDto to all the listeners registered in the system in a way that
   * guarantees all of them will be called without exceptions in any one interrupting the execution
   * flow.
   */
  public void dispatchEvent(TaskStatusChangeEventDto eventDto) {
    for (TaskStatusChangeEventListener listener : taskStatusChangeEventListenerList) {
      try {
        listener.handleEvent(eventDto);
      } catch (Exception e) {
        log.error("Exception while invoking an event listener", e);
      }
    }
  }
}
