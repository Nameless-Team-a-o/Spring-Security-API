package com.nameless.service;

import com.nameless.dto.SubmissionDTO;
import com.nameless.entity.question.model.Question;
import com.nameless.entity.question.repository.QuestionRepository;
import com.nameless.entity.submission.model.Submission;
import com.nameless.entity.submission.repository.SubmissionRepository;
import com.nameless.entity.user.model.User;
import com.nameless.entity.user.repository.UserRepository;
import com.nameless.exception.SubmissionProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;
    private  final UserRepository userRepository;

    public SubmissionDTO getSubmissionById(Long submissionId) {
        Optional<Submission> optionalSubmission = submissionRepository.findById(submissionId);
        if (optionalSubmission.isPresent()) {
            Submission submission = optionalSubmission.get();
            return SubmissionDTO.builder()
                    .id(submission.getId())
                    .code(submission.getCode())
                    .expectedOutput(submission.getExpectedOutput())
                    .result(submission.getResult())
                    .status(submission.getStatus())
                    .createdAt(submission.getCreatedAt())
                    .build();
        } else {
            throw new SubmissionProcessingException("Submission not found with id: " + submissionId);
        }
    }


    // Retrieves all submissions for a specific question and user
    public List<Submission> getSubmissionsForQuestion(Long questionId) {
        // Extract user ID from the security context (from the JWT token)
        String username = getAuthenticatedUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find the question entity by its ID
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Retrieve all submissions for this user and question
        return submissionRepository.findByUserAndQuestion(user, question);
    }

    // Helper method to get the authenticated user's username
    private String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Extract username from the token
        } else {
            return principal.toString();
        }
    }

}