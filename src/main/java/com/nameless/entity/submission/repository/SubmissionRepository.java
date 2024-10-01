package com.nameless.entity.submission.repository;

import com.nameless.entity.question.model.Question;
import com.nameless.entity.submission.model.Submission;
import com.nameless.entity.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserAndQuestion(User user, Question question);

}