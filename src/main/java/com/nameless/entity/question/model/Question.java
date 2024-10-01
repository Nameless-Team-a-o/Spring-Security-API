package com.nameless.entity.question.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the question

    @Column(nullable = false, length = 100) // Ensure title is not null and limit length
    private String title; // Title of the question

    @Column(nullable = false, length = 2000) // Ensure description is not null and limit length
    private String description; // Description of the question

    @Column(nullable = false) // Ensure expectedOutput cannot be null
    private String expectedOutput; // The expected output that the user's code should produce

    @Column(nullable = false) // Ensure testCase cannot be null
    private String testCase; // Test case input for validating user code


}
