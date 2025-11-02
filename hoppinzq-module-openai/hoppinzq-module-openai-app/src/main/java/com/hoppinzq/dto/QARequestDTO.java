package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QARequestDTO {
    private String question;
    private String type;
    private String method = "HYBRID";
    private int resultNum;
    private float threshold;
    private String attrKey;
    private List<String> labelName;
}
