package com.community.tools.service.github.event.listeners;

import com.community.tools.dto.events.tasks.TaskStatusChangeEventDto;
import com.community.tools.model.Mentors;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import com.community.tools.repository.MentorsRepository;
import com.community.tools.repository.UserRepository;
import com.community.tools.service.MentorNotificationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adds mentor to trainee if trainee and mentor identified by the respective fields (trainee and
 * reviewer git names) in TaskStatusChangeEventDto can be found in the system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReviewMentorAddingEventListener implements TaskStatusChangeEventListener {

  private final MentorNotificationService mentorNotificationService;
  private final MentorsRepository mentorsRepository;
  private final UserRepository userRepository;
  private static final String NOTIFICATION_TEMPLATE =
      "You have been added as a mentor of trainee %s because you reviewed their "
          + "pull request %s of the task %s";
  private static final String TRAINEE_NOT_FOUND_MESSAGE_TEMPLATE =
      "Trainee with git login %s could not be found";

  @Override
  public void handleEvent(TaskStatusChangeEventDto eventDto) {
    if (eventDto.getTaskStatus()
        .equals(TaskStatus.CHANGES_REQUESTED) && eventDto.getReviewerGitName() != null) {
      Optional<Mentors> maybeReviewer = mentorsRepository.findByGitNick(
          eventDto.getReviewerGitName());
      maybeReviewer.ifPresent(it -> {
        boolean isAdditionSuccessful = addMentorToTrainee(it, eventDto.getTraineeGitName());
        if (isAdditionSuccessful) {
          mentorNotificationService.notifyMentor(eventDto.getReviewerGitName(),
              String.format(NOTIFICATION_TEMPLATE, eventDto.getTraineeGitName(),
                  eventDto.getPullUrl(), eventDto.getTaskName()));
        }
      });
    }
  }

  /**
   * Adds mentor to trainee if trainee identified by the given git login is registered in the
   * system.
   *
   * @return true if addition was successful, false if trainee already has this mentor
   */
  private boolean addMentorToTrainee(Mentors mentor,
      String gitNameTrainee) {
    Optional<User> maybeUser = userRepository.findByGitName(gitNameTrainee);
    if (!maybeUser.isPresent()) {
      throw new RuntimeException(String.format(TRAINEE_NOT_FOUND_MESSAGE_TEMPLATE, gitNameTrainee));
    }
    User user = maybeUser.get();
    if (user.getMentors().contains(mentor)) {
      return false;
    }
    user.getMentors().add(mentor);
    userRepository.saveAndFlush(user);
    return true;
  }
}
