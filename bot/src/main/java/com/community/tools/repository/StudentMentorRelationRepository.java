package com.community.tools.repository;

import com.community.tools.model.StudentMentorRelation;
import com.community.tools.model.StudentMentorRelationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentMentorRelationRepository extends
    JpaRepository<StudentMentorRelation, StudentMentorRelationId> {

}
