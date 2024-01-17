package com.community.tools.repository.stats;

import com.community.tools.model.stats.UserTask;
import com.community.tools.model.stats.UserTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, UserTaskId> {

}
