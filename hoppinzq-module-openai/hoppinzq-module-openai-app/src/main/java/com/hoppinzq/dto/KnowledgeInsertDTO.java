package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeInsertDTO {
    private String key;
    private String name;
    private List<String> labels;
}

