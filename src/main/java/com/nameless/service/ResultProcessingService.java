package com.nameless.service;

import com.nameless.dto.ResultResponseDTO;
import com.nameless.entity.result.Result;
import com.nameless.entity.result.ResultRepository;
import com.nameless.entity.submission.Submission;
import com.nameless.entity.submission.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final SubmissionRepository submissionRepository;
    private final ResultRepository resultRepository;

    public void processResult(ResultResponseDTO resultResponse) {
        Submission submission = findSubmissionById(resultResponse.getSubmissionId());

        updateSubmission(submission, resultResponse.getStatus().getDescription());
        saveResult(submission, resultResponse);
    }

    private Submission findSubmissionById(String submissionIdString) {
        Long submissionId = Long.valueOf(submissionIdString);
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    }

    private void updateSubmission(Submission submission, String statusDescription) {
        submission.setStatus("COMPLETED");
        submission.setResult(statusDescription);
        submissionRepository.save(submission);
    }

    private void saveResult(Submission submission, ResultResponseDTO resultResponse) {
        Result result = Result.builder()
                .submission(submission)
                .verdict(resultResponse.getStatus().getDescription())
                .executionTime(resultResponse.getTime())
                .memoryUsed(resultResponse.getMemory())
                .createdAt(LocalDateTime.now())
                .build();
        resultRepository.save(result);
    }
}
