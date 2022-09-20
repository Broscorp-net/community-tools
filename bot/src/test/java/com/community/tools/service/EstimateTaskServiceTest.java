package com.community.tools.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.community.tools.model.Estimate;
import com.community.tools.model.Task;
import com.community.tools.repository.EstimateRepository;
import com.community.tools.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class EstimateTaskServiceTest {

  @MockBean
  private TaskRepository taskRepository;

  @MockBean
  private EstimateRepository estimateRepository;

  @Autowired
  private EstimateTaskService estimateTaskService;

  @Test
  public void testSaveEstimateTask_whenTaskIsNew() {
    String userId = "U01RE5SFMFV";
    Integer taskNumber = 1;
    Integer estimateId = 3;
    String estimateName = "useful";

    when(taskRepository.findByUserIdAndTaskNumber(userId, taskNumber)).thenReturn(null);

    when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    when(estimateRepository.getOne(estimateId)).thenReturn(new Estimate(estimateId, estimateName));

    Task savedTask = estimateTaskService.saveEstimateTask(userId, taskNumber, estimateId);

    Assert.assertEquals(userId, savedTask.getUserId());
    Assert.assertEquals(taskNumber, savedTask.getTaskNumber());
    Assert.assertEquals(estimateId, savedTask.getEstimate().getId());
    Assert.assertEquals(estimateName, savedTask.getEstimate().getName());
  }

  @Test
  public void testSaveEstimateTask_whenTaskAlreadyExists() {
    String userId = "U01RE5SFMFV";
    Integer taskNumber = 1;
    Integer estimateId = 3;
    String estimateName = "useful";

    when(taskRepository.findByUserIdAndTaskNumber(userId, taskNumber)).thenReturn(Task.builder()
        .taskNumber(taskNumber)
        .userId(userId)
        .estimate(new Estimate(1, "useless"))
        .build());

    when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    when(estimateRepository.getOne(estimateId)).thenReturn(new Estimate(estimateId, estimateName));

    Task savedTask = estimateTaskService.saveEstimateTask(userId, taskNumber, estimateId);

    Assert.assertEquals(userId, savedTask.getUserId());
    Assert.assertEquals(taskNumber, savedTask.getTaskNumber());
    Assert.assertEquals(estimateId, savedTask.getEstimate().getId());
    Assert.assertEquals(estimateName, savedTask.getEstimate().getName());
  }
}
