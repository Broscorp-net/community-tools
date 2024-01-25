package com.community.tools.service;

import com.community.tools.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageListener implements EventListener {
  private TrackingService trackingService;

  @Value("${testModeSwitcher}")
  private Boolean testModeSwitcher;

  @Autowired
  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }

  @Override
  public void memberJoin(Message message) {
    String userId = message.getUserId();
    try {
      trackingService.resetUser(userId);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void messageReceived(Message message) {
    String messageFromUser = message.getText();
    String userId = message.getUserId();

    try {
      if (messageFromUser.equalsIgnoreCase("reset")
          && testModeSwitcher) {
        trackingService.resetUser(userId);
      } else {
        trackingService.doAction(messageFromUser, userId);
      }
    } catch (Exception exception) {
      throw new RuntimeException("Impossible to answer request with id = "
          + userId, exception);
    }
  }
}