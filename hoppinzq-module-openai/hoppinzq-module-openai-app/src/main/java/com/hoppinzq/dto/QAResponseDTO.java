package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QAResponseDTO {
    private String source;
    private String type;
    private String content;
    private String title;
}
