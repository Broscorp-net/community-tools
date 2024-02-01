package com.community.tools.service.github.event.listeners;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;

public interface TaskStatusChangeEventListener {

  /**
   * Processes an object with event information of type T passed to it.
   *
   * @param event event info
   */
  void handleEvent(TaskStatusChangeEventDto event);
}
