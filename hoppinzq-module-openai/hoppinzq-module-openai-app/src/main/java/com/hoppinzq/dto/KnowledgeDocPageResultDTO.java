package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KnowledgeDocPageResultDTO {
    private String doc_id;
    private String doc_name;
    private String doc_type;
    private String creator;
    private String date;
    private String doc_url;
    private String attr_str;
    private String label_str;
    private String knowledge_id;
}

