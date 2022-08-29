package com.community.tools.service;

import com.community.tools.model.Estimate;
import com.community.tools.model.Task;
import com.community.tools.repository.EstimateRepository;
import com.community.tools.repository.TaskRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class EstimateTaskServiceTest {

  private String userId;
  private Integer taskNumber;
  private Integer estimateId;

  @Autowired
  private EstimateTaskService estimateTaskService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private EstimateRepository estimateRepository;

  /**
   * This method init fields in the EstimateTaskService.
   */
  @Before
  public void setUp() {
    userId = "U01RE5SFMFV";
    taskNumber = 1;
    estimateId = 3;

    estimateRepository.save(new Estimate(estimateId, "test estimate"));
  }

  @Test
  @Transactional
  public void saveEstimateTaskTest() {
    Integer taskId = estimateTaskService.saveEstimateTask(userId, taskNumber, estimateId).getId();
    Task task = taskRepository.getOne(taskId);

    Assert.assertEquals(userId, task.getUserId());
    Assert.assertEquals(taskNumber, task.getTaskNumber());
    Assert.assertEquals(estimateRepository.getOne(estimateId), task.getEstimate());

  }
}
