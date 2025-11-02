package com.hoppinzq.model.openai.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/files
 */
@Data
public class File {
    String id;
    String object;
    Long bytes;
    @JsonProperty("created_at")
    Long createdAt;
    String filename;
    String purpose;
    String status;
    @JsonProperty("status_details")
    String statusDetails;
}
