package com.community.tools.service;

import com.community.tools.model.EmailBuild;
import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.payload.EstimatePayload;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SimplePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingService {

  private final MessageService<?> messageService;
  private final StateMachineService stateMachineService;
  private final UserRepository userRepository;
  private final EstimateTaskService estimateTaskService;
  private final EmailService emailService;

  /**
   * Method to start the event by state.
   *
   * @param message message from user
   * @throws Exception Exception
   */
  public void doAction(Message message) throws Exception {
    String userId = message.getAuthor().getId();
    String messageFromUser = message.getContentRaw();

    StateMachine<State, Event> machine = stateMachineService.restoreMachine(userId);
    String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

    String messageToSend = Messages.DEFAULT_MESSAGE;
    Event event = null;
    Payload payload = null;

    switch (machine.getState().getId()) {
      case NEW_USER:
        messageToSend = Messages.MESSAGE_NOT_WELCOME;
        break;

      case RULES:
        if (messageFromUser.equalsIgnoreCase("ready")) {
          payload = new SimplePayload(userId);
          event = Event.QUESTION_FIRST;
        } else {
          messageToSend = Messages.NOT_THAT_MESSAGE;
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
          messageToSend = Messages.NOT_THAT_MESSAGE;
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
          messageToSend = Messages.CHOOSE_AN_ANSWER;
        }
        break;
      case GOT_THE_TASK:
        if (messageFromUser.equalsIgnoreCase("yes")) {
          estimateTaskService.estimate(userId);
          return;
        } else if (messageFromUser.equalsIgnoreCase("no")) {
          event = Event.RESENDING_ESTIMATE_TASK;
          payload = new SimplePayload(userId);
        }
        break;
      default:
        event = null;
        payload = null;
    }

    if (event == null) {
      Pattern pattern = Pattern.compile("^(.+)@([^@]+[^.])\\.(.+)$");
      Matcher matcher = pattern.matcher(messageFromUser);
      if (matcher.matches()) {
        emailService.sendEmail(EmailBuild.builder()
            .userEmail(messageFromUser.trim())
            .subject("Bro_Bot Email")
            .text(Messages.EMAIL)
            .build());
      } else {
        messageService.sendPrivateMessage(messageService.getUserById(userId), messageToSend);
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
  public void resetUser(String userId, String guildId) throws Exception {

    User stateEntity = new User();
    stateEntity.setUserID(userId);
    stateEntity.setGuildId(guildId);
    stateEntity.setDateRegistration(LocalDate.now());
    String userName = messageService.getUserById(userId);
    userRepository.save(stateEntity);
    stateMachineService.persistMachineForNewUser(userId);

    messageService.sendPrivateMessage(userName, Messages.WELCOME);


  }
}
