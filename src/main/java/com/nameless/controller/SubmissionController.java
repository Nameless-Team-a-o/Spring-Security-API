package com.nameless.entity.submission;

import com.nameless.dto.SubmissionRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionProducer submissionProducer;

    @PostMapping
    public ResponseEntity<String> submitCode(@RequestBody SubmissionRequestDTO request) {
        boolean isSubmissionSuccessful = submissionProducer.createSubmission(request);

        if (isSubmissionSuccessful) {
            return new ResponseEntity<>("Submission received.", HttpStatus.CREATED); // Response after receiving the submission
        } else {
            return new ResponseEntity<>("Failed to process submission.", HttpStatus.BAD_REQUEST); // Handle the case when submission fails
        }
    }
}
