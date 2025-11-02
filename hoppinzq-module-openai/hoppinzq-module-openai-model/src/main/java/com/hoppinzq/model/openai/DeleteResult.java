package com.hoppinzq.model.openai;

import lombok.Data;

@Data
public class DeleteResult {
    private String id;
    private String object;
    private boolean deleted;
}
