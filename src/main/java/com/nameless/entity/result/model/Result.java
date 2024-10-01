package com.nameless.entity.result.model;

import com.nameless.entity.submission.model.Submission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    private String verdict; // Result of the execution (e.g., PASSED, FAILED)
    private float executionTime; // Time taken for execution
    private float memoryUsed; // Memory used during execution
    private LocalDateTime createdAt; // Timestamp of when the result was created
}