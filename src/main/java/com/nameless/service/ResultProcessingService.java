package com.nameless.service;

import com.nameless.dto.ResultResponseDTO;
import com.nameless.entity.result.model.Result;
import com.nameless.entity.result.repository.ResultRepository;
import com.nameless.entity.submission.model.Submission;
import com.nameless.entity.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResultProcessingService {

    private final SubmissionRepository submissionRepository;
    private final ResultRepository resultRepository;

    public void processResult(ResultResponseDTO resultResponse) {
        Submission submission = findSubmissionById(resultResponse.getSubmissionId());

        updateSubmission(submission, resultResponse.getStatus().getDescription() , resultResponse.getStdout()) ;
        saveResult(submission, resultResponse);
    }

    private Submission findSubmissionById(String submissionIdString) {
        Long submissionId = Long.valueOf(submissionIdString);
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    }

    private void updateSubmission(Submission submission, String statusDescription , String stdout) {
        submission.setStatus("COMPLETED");
        submission.setResult(statusDescription);
        submission.setActualOutput(stdout);
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
