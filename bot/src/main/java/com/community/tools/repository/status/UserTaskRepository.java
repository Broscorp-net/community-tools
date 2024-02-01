package com.community.tools.repository.status;

import com.community.tools.model.status.UserTask;
import com.community.tools.model.status.UserTaskId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, UserTaskId> {

  List<UserTask> findUserTasksByLastActivityAfter(LocalDate earliestActivity);
}
