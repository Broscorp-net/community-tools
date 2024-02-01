package com.community.tools.repository;

import com.community.tools.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByUserId(String userId);

  Optional<User> findByGitName(String gitName);

  List<User> findByGitNameIn(List<String> githubUsernames);
}
