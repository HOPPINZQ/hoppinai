package com.hoppinzq.model.openai.messages.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCitation {
    @JsonProperty("file_id")
    String fileId;
    String quote;
}
