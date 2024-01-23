package com.community.tools.service;

import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes event object of type T passed to it, by passing them to all the registered event
 * handlers.
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventProcessingService<T> {

  private final List<EventHandler<T>> eventHandlers;

  /**
   * Method that allows passing an event to all the handlers registered with the processing service
   * with a guarantee that execution flow will not be interrupted by exceptions within the
   * user-defined handlers.
   *
   * @param event event info object.
   */
  public void processEvent(T event) {
    eventHandlers.forEach(eventHandler -> {
      try {
        eventHandler.handleEvent(event);
      } catch (Exception e) {
        log.error("Exception while invoking an event handler", e);
      }
    });
  }
}
