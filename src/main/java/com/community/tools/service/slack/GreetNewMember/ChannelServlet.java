package com.community.tools.service.slack.GreetNewMember;

import com.github.seratch.jslack.app_backend.events.EventsDispatcher;
import com.github.seratch.jslack.app_backend.events.servlet.SlackEventsApiServlet;

public class ChannelServlet extends SlackEventsApiServlet {

  @Override
  protected void setupDispatcher(EventsDispatcher dispatcher) {
    dispatcher.register(new ChannelEvent().channelCreatedHandler);
  }
}
