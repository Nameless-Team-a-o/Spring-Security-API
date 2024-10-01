package com.nameless.entity.submission.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nameless.entity.question.model.Question;
import com.nameless.entity.user.model.User;
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
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Prevent serialization of the User reference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore // Prevent serialization of the Question reference
    private Question question;
    @Column( unique = true )
    private String code; // The code submitted by the user
    private String expectedOutput;// The expected output for the question
    private String actualOutput;
    private String status; // Possible statuses: PENDING, PROCESSING, COMPLETED
    private String result; // The actual result after code execution
    private LocalDateTime createdAt; // Timestamp when the submission was created
}