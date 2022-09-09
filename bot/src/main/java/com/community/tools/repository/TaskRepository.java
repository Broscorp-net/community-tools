package com.community.tools.repository;

import com.community.tools.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * we are not sure is it work
 * refactor it or delete
 */
@Deprecated
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

  Task findByUserIdAndTaskNumber(String userId, Integer taskNumber);
}
