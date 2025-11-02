package com.hoppinzq.model.openai.edit;

import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/edits/create
 */
@Data
@Deprecated
public class EditUsage {
    String promptTokens;
    String completionTokens;
    String totalTokens;
}
