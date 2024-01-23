package com.community.tools.service.github.event;

public interface EventHandler<T> {

  /**
   * Processes an object with event information of type T passed to it.
   *
   * @param event event info
   */
  void handleEvent(T event);
}
