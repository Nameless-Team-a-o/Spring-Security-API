package com.nameless.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionDTO {
    private Long id;
    private String code;
    private String expectedOutput;
    private String result;
    private String status;
    private LocalDateTime createdAt;
}