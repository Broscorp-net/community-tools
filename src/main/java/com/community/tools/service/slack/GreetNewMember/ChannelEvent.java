package com.community.tools.service.slack.GreetNewMember;

import com.github.seratch.jslack.app_backend.events.handler.ChannelCreatedHandler;
import com.github.seratch.jslack.app_backend.events.payload.ChannelCreatedPayload;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChannelEvent {

 public ChannelCreatedHandler channelCreatedHandler = new ChannelCreatedHandler() {
    @Override
    public void handle(ChannelCreatedPayload teamJoinPayload) {
      System.out.println("Я тригернулся, и могбы что то сделать");
    }
  };

}
