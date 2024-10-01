package com.nameless.service;

import com.nameless.entity.result.model.Result;
import com.nameless.entity.result.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class ResultService {

    private final ResultRepository resultRepository;

    public Result getResultBySubmissionId(Long submissionId) {
        Optional<Result> optionalResult = resultRepository.findBySubmissionId(submissionId);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        } else {
            throw new RuntimeException("Result not found for submission id: " + submissionId);
        }
    }
}