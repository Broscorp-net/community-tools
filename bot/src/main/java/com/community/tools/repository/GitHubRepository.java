package com.community.tools.repository;

import com.community.tools.model.Bro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Deprecated
@Repository
public interface GitHubRepository extends JpaRepository<Bro, Long> {
}
