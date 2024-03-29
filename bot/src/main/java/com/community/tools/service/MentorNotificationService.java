package com.community.tools.service;

import com.community.tools.model.User;
import com.community.tools.repository.MentorsRepository;
import com.community.tools.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorNotificationService {

  private final MessageService<MessageEmbed> messageService;
  private final MentorsRepository mentorsRepository;
  private final UserRepository userRepository;

  /**
   * Sends notifications to all mentors of the trainee identified by the given git name. If no
   * mentor is associated with the trainee, the notification message is sent to all mentors the
   * system is aware of.
   *
   * @param traineeGitName git login of the trainee.
   * @param message        message to be sent to mentors associated with the trainee.
   */
  public void notifyAllTraineeMentors(String traineeGitName, String message) {
    Optional<User> maybeTrainee = userRepository.findByGitName(traineeGitName);
    maybeTrainee.ifPresent(trainee -> {
      if (trainee.getMentors().isEmpty()) {
        notifyAllMentors(message);
      } else {
        trainee.getMentors().forEach(
            mentor -> mentorsRepository.findByGitNick(mentor.getGitNick())
                .ifPresent(it -> messageService.sendPrivateMessage(it.getDiscordName(), message)));
      }
    });
  }

  /**
   * Sends notification with a specified message to all mentors.
   *
   * @param message message to be sent to mentors.
   */
  public void notifyAllMentors(String message) {
    mentorsRepository.findAll().forEach(mentor -> {
      if (mentor.getDiscordName() != null) {
        messageService.sendPrivateMessage(mentor.getDiscordName(),
            message);
      }
    });
  }

  /**
   * Attempts to send a message to the mentor identified by the given mentorGitName is such mentor
   * can be found on the record.
   *
   * @param mentorGitName mentor to be notified.
   * @param message       message to be sent to the mentor.
   */
  public void notifyMentor(String mentorGitName, String message) {
    mentorsRepository.findByGitNick(mentorGitName)
        .ifPresent(mentors -> messageService.sendPrivateMessage(mentors.getDiscordName(), message));
  }
}
