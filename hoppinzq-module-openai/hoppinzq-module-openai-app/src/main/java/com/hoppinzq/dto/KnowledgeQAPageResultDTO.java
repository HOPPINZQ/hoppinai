package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KnowledgeQAPageResultDTO {
    private String qa_id;
    private String answer;
    private String question;
    private String creator;
    private String date;
    private String knowledge_id;
    private String attr_str;
    private String label_str;
}

