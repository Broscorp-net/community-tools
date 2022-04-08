package com.community.tools.service;

import com.community.tools.model.Message;

public interface EventListener {

  void memberJoin(Message message);

  void messageReceived(Message message);

  void messageWelcome(Message message);
}
