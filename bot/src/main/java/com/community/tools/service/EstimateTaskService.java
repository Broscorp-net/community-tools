package com.community.tools.service;

import com.community.tools.model.Task;
import com.community.tools.repository.EstimateRepository;
import com.community.tools.repository.TaskRepository;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateTaskService {
  private final TaskRepository taskRepository;
  private final EstimateRepository estimateRepository;
  private final StateMachineService stateMachineService;
  private final GiveNewTaskService giveNewTaskService;

  /**
   * Save estimate task in database.
   *
   * @param userId     - id users
   * @param taskNumber - taskNumber for saving
   * @param estimateId - id estimate
   */
  @Transactional
  public Task saveEstimateTask(String userId, Integer taskNumber, Integer estimateId) {
    Task task = taskRepository.findByUserIdAndTaskNumber(userId, taskNumber);
    if (task == null) {
      task = Task.builder()
          .userId(userId)
          .taskNumber(taskNumber)
          .estimate(estimateRepository.getOne(estimateId))
          .build();
    } else {
      task.setEstimate(estimateRepository.getOne(estimateId));
    }
    return taskRepository.save(task);
  }

  /**
   * Method for save ask estimate and get new task.
   *
   * @param userId - id users
   */
  public void estimate(String userId) throws Exception {
    StateMachine<State, Event> machine = stateMachineService.restoreMachine(userId);
    Integer taskNumber = (Integer) machine.getExtendedState().getVariables().get("taskNumber");
    Integer value = (Integer) machine.getExtendedState().getVariables().get("value");

    saveEstimateTask(userId, taskNumber, value);
    giveNewTaskService.giveNewTask(machine, userId, taskNumber);
  }
}
