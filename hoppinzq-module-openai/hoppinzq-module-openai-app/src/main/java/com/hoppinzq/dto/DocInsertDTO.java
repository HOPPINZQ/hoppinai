package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocInsertDTO {
    private String fileName;
    private String fileType;
    private String fileUrl;
    private List<KnowledgeAttrDTO> knowledgeAttrInsertDTOS;
}

