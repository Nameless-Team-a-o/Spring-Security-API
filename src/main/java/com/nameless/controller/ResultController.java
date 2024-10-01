package com.nameless.controller;

import com.nameless.entity.result.model.Result;
import com.nameless.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping("/{submissionId}")
    public ResponseEntity<Result> getResultBySubmissionId(@PathVariable Long submissionId) {
        Result result = resultService.getResultBySubmissionId(submissionId);
        return ResponseEntity.ok(result);
    }
}