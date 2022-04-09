package com.community.tools.service;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {

  @Autowired private MessageService messageService;

  @Autowired private StateMachineService stateMachineService;

  @Autowired private StateMachineRepository stateMachineRepository;

  @Autowired private EstimateTaskService estimateTaskService;

  @Autowired private MessageConstructor messageConstructor;

  @Autowired private EmailService emailService;

  /**
   * Method to start the event by state.
   *
   * @param messageFromUser message from user
   * @param userId user id
   * @throws Exception Exception
   */
  public void doAction(String messageFromUser, String userId) throws Exception {

    StateMachine<State, Event> machine = stateMachineService.restoreMachine(userId);
    String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

    String message = Messages.DEFAULT_MESSAGE;
    Event event = null;
    Payload payload = null;

    switch (machine.getState().getId()) {
      case NEW_USER:
        if (messageFromUser.equalsIgnoreCase("ready")) {
          payload = new SimplePayload(userId);
          event = Event.QUESTION_FIRST;
        } else {
          message = Messages.NOT_THAT_MESSAGE;
        }
        break;
      case FIRST_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.QUESTION_SECOND;
        break;
      case SECOND_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.QUESTION_THIRD;
        break;
      case THIRD_QUESTION:
        payload = new QuestionPayload(userId, messageFromUser, userForQuestion);
        event = Event.CONSENT_TO_INFORMATION;
        break;
      case AGREED_LICENSE:
        payload = new VerificationPayload(userId, messageFromUser);
        event = Event.LOGIN_CONFIRMATION;
        break;
      case CHECK_LOGIN:
        if (messageFromUser.equalsIgnoreCase("yes")) {
          event = Event.ADD_GIT_NAME_AND_FIRST_TASK;
        } else if (messageFromUser.equalsIgnoreCase("no")) {
          event = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
        } else {
          message = Messages.NOT_THAT_MESSAGE;
        }
        payload =
            (VerificationPayload) machine.getExtendedState().getVariables().get("dataPayload");
        break;
      case ESTIMATE_THE_TASK:
        int value = Integer.parseInt(messageFromUser);
        if (value > 0 && value < 6) {
          event = Event.CONFIRM_ESTIMATE;
          payload = new EstimatePayload(userId, value);
        } else {
          message = Messages.CHOOSE_AN_ANSWER;
        }
        break;
      case GOT_THE_TASK:
        if (messageFromUser.equals("yes")) {
          estimateTaskService.estimate(userId);
          return;
        } else if (messageFromUser.equals("no")) {
          event = Event.RESENDING_ESTIMATE_TASK;
          payload = new SimplePayload(userId);
        }
        break;
      default:
        event = null;
        payload = null;
    }
    if (event == null) {
      if (messageFromUser.indexOf("@") != -1 && messageFromUser.indexOf(".") != -1) {
        emailService.sendEmail(messageFromUser.trim());
      } else {
        messageService.sendPrivateMessage(messageService.getUserById(userId), message);
      }
    } else {
      stateMachineService.doAction(machine, payload, event);
    }
  }

  /**
   * Reset User with userId.
   *
   * @param userId platform user id
   * @throws Exception Exception
   */
  public void resetUser(String userId) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(userId);
    String userName = messageService.getUserById(userId);
    stateMachineRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(userId);

    messageService.sendPrivateMessage(userName, Messages.WELCOME);
    messageService.sendBlocksMessage(
        userName,
        messageConstructor.createMessageAboutRules(
            Messages.MESSAGE_ABOUT_RULES_1,
            Messages.MESSAGE_ABOUT_RULES_2,
            Messages.MESSAGE_ABOUT_RULES_3,
            Messages.MESSAGE_ABOUT_RULES_4));
  }
}
