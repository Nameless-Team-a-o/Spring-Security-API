package com.nameless.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRequestDTO {
    private Long userId;
    private String languageId;
    private Long questionId;
    private String code;
}
