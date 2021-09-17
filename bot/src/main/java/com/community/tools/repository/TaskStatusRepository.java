package com.community.tools.repository;

import com.community.tools.model.TaskStatus;
import com.community.tools.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

  Optional<TaskStatus> findTaskStatusByUserAndTaskName(User user, String taskName);

  List<TaskStatus> findAllByUser(User user);
}
