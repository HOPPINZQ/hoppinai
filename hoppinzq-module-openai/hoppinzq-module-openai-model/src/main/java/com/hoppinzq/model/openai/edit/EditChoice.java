package com.hoppinzq.model.openai.edit;

import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/edits/create
 */
@Data
public class EditChoice {
    String text;
    Integer index;
}
