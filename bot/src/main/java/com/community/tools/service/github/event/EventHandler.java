package com.community.tools.service.github.event;

public interface EventHandler<T> {

  /**
   * Processes an event object of type T passed to it.
   *
   * @param eventJson GitHub hook event
   */
  void handleEvent(T eventJson);
}
