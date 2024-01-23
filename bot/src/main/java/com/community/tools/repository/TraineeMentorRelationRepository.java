package com.community.tools.repository;

import com.community.tools.model.TraineeMentorRelation;
import com.community.tools.model.TraineeMentorRelationId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraineeMentorRelationRepository extends
    JpaRepository<TraineeMentorRelation, TraineeMentorRelationId> {

  List<TraineeMentorRelation> findAllByGitNameTrainee(String gitNameTrainee);
}
