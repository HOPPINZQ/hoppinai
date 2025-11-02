package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewriteMessageDTO {

    private String role;
    private String content;
    private String reasoningContent;
}

