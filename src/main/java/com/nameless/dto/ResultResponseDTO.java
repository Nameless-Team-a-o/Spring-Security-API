package com.nameless.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponseDTO {
    private Status status;
    private String submissionId;
    private Float time;
    private Float memory;
    private String stdout;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private int id;
        private String description;
    }
}