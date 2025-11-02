package com.hoppinzq.model.openai.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * https://beta.openai.com/docs/api-reference/edits/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditRequest {
    String model;
    String input;
    @NonNull
    String instruction;
    Integer n;
    Double temperature;
    @JsonProperty("top_p")
    Double topP;
}
