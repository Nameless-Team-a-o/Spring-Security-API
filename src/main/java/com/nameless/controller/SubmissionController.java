package com.nameless.controller;

import com.nameless.dto.SubmissionDTO;
import com.nameless.dto.SubmissionRequestDTO;
import com.nameless.entity.submission.model.Submission;
import com.nameless.exception.SubmissionProcessingException;
import com.nameless.queue.SubmissionProducer;
import com.nameless.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionProducer submissionProducer;
    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<String> submitCode(@RequestBody SubmissionRequestDTO request) {
        boolean isSubmissionSuccessful = submissionProducer.createSubmission(request);

        if (isSubmissionSuccessful) {
            return new ResponseEntity<>("Submission received.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Failed to process submission.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable Long submissionId) {
        try {
            SubmissionDTO submissionDTO = submissionService.getSubmissionById(submissionId);
            return ResponseEntity.ok(submissionDTO);
        } catch (SubmissionProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    // GET endpoint to retrieve all submissions for a question by the authenticated user
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Submission>> getSubmissionsForQuestion(@PathVariable Long questionId) {
        List<Submission> submissions = submissionService.getSubmissionsForQuestion(questionId);
        return ResponseEntity.ok(submissions);
    }


}
