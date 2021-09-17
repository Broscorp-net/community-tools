package com.community.tools.repository;

import com.community.tools.model.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimateRepository extends JpaRepository<Estimate, Integer> {

}
