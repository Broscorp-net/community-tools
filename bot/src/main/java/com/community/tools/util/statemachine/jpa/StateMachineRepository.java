package com.community.tools.util.statemachine.jpa;

import com.community.tools.model.User;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMachineRepository extends JpaRepository<User, Long> {

  Optional<User> findByUserID(String userID);

  Optional<User> findByGitName(String gitName);
}
