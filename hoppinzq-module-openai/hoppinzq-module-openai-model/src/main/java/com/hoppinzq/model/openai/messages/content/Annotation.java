package com.hoppinzq.model.openai.messages.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
    String type;
    String text;
    @JsonProperty("file_citation")
    FileCitation fileCitation;
    @JsonProperty("file_path")
    FilePath filePath;
    @JsonProperty("start_index")
    int startIndex;
    @JsonProperty("end_index")
    int endIndex;
}
