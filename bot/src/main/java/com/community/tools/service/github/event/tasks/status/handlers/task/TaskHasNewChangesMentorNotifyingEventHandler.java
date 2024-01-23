package com.community.tools.service.github.event.tasks.status.handlers.task;

import com.community.tools.dto.events.tasks.TaskHasNewChangesEventDto;
import com.community.tools.model.TraineeMentorRelation;
import com.community.tools.repository.MentorsRepository;
import com.community.tools.repository.TraineeMentorRelationRepository;
import com.community.tools.service.MessageService;
import com.community.tools.service.github.event.EventHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskHasNewChangesMentorNotifyingEventHandler implements
    EventHandler<TaskHasNewChangesEventDto> {

  private final MessageService<MessageEmbed> messageService;
  private final MentorsRepository mentorsRepository;
  private final TraineeMentorRelationRepository traineeMentorRelationRepository;

  @Override
  public void handleEvent(TaskHasNewChangesEventDto eventDto) {
    List<TraineeMentorRelation> traineeMentorRelations = traineeMentorRelationRepository.findAllByGitNameTrainee(
        eventDto.getTraineeGitName());
    if (traineeMentorRelations.isEmpty()) {
      mentorsRepository.findAll().forEach(mentor -> {
        if (mentor.getDiscordName() != null) {
          messageService.sendPrivateMessage(mentor.getDiscordName(),
              formNotificationString(eventDto));
        }
      });
    } else {
      traineeMentorRelations.forEach(
          mentor -> messageService.sendPrivateMessage(mentor.getGitNameMentor(),
              formNotificationString(eventDto)));
    }
  }

  private String formNotificationString(TaskHasNewChangesEventDto eventDto) {
    return "Task " + eventDto.getTaskName() + " by " + eventDto.getTraineeGitName() + " at "
        + eventDto.getPullUrl() + " has New Changes to Review!";
  }
}
