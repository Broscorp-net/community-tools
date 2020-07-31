package com.community.tools.util.statemachie.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMachineRepository extends JpaRepository<StateEntity,Long> {
    Optional<StateEntity> findByUserID (String userID);
    Optional<StateEntity> findByGitName (String gitName);
}
