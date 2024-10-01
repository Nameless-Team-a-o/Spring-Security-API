package com.nameless.entity.result.repository;

import com.nameless.entity.result.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findBySubmissionId(Long submissionId);
}
