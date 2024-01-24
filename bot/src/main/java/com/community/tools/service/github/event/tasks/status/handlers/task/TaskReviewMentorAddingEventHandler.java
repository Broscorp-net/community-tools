package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskStatusEventDto;
import com.community.tools.model.Mentors;
import com.community.tools.model.TaskStatus;
import com.community.tools.model.TraineeMentorRelation;
import com.community.tools.model.TraineeMentorRelationId;
import com.community.tools.repository.MentorsRepository;
import com.community.tools.repository.TraineeMentorRelationRepository;
import com.community.tools.service.MentorNotificationService;
import com.community.tools.service.github.event.EventHandler;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReviewMentorAddingEventHandler implements EventHandler<TaskStatusEventDto> {

  private final MentorNotificationService mentorNotificationService;
  private final MentorsRepository mentorsRepository;
  private final TraineeMentorRelationRepository traineeMentorRelationRepository;
  private static final String NOTIFICATION_TEMPLATE =
      "You have been added as a mentor of trainee %s because you reviewed their "
          + "pull request %s of the task %s";

  @Override
  public void handleEvent(TaskStatusEventDto eventDto) {
    if (eventDto.getTaskStatus()
        .equals(TaskStatus.CHANGES_REQUESTED) && eventDto.getReviewerGitName() != null) {
      Optional<Mentors> maybeReviewer = mentorsRepository.findByGitNick(
          eventDto.getReviewerGitName());
      maybeReviewer.ifPresent(it -> {
        Optional<TraineeMentorRelation> maybeRelation = traineeMentorRelationRepository.findById(
            new TraineeMentorRelationId(it.getGitNick(),
                eventDto.getTraineeGitName()));
        if (!maybeRelation.isPresent()) {
          traineeMentorRelationRepository.saveAndFlush(new TraineeMentorRelation(it.getGitNick(),
              eventDto.getTraineeGitName()));
          mentorNotificationService.notifyMentor(eventDto.getReviewerGitName(),
              String.format(NOTIFICATION_TEMPLATE, eventDto.getTraineeGitName(),
                  eventDto.getPullUrl(), eventDto.getTaskName()));
        }
      });
    }
  }
}