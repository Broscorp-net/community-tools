package com.community.tools.service;

import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Processes event object of type T passed to it, by passing them to all the registered event
 * handlers.
 */
@RequiredArgsConstructor
public abstract class AbstractEventProcessingService<T> {

  private final List<EventHandler<T>> eventHandlers;

  public void processEvent(T eventJson) {
    eventHandlers.forEach(eventHandler -> eventHandler.handleEvent(eventJson));
  }
}
