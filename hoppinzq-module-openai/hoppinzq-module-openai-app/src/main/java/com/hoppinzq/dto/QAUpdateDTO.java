package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QAUpdateDTO {
    private String question;
    private String answer;
    private String qaId;
    private List<KnowledgeAttrDTO> knowledgeAttrUpdateDTOS;
}

