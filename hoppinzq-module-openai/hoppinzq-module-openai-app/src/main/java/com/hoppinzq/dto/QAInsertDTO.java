package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QAInsertDTO {
    private String question;
    private String answer;
    private List<KnowledgeAttrDTO> knowledgeAttrInsertDTOS;
}

