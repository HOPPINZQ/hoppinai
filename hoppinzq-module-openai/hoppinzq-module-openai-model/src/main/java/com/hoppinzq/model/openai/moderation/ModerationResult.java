package com.hoppinzq.model.openai.moderation;

import lombok.Data;

import java.util.List;

@Data
public class ModerationResult {
    private String id;
    private String model;
    private List<Moderation> results;
}
