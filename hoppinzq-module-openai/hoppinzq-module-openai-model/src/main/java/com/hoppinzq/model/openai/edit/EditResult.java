package com.hoppinzq.model.openai.edit;

import com.hoppinzq.model.openai.Usage;
import lombok.Data;

import java.util.List;

/**
 * https://beta.openai.com/docs/api-reference/edits/create
 */
@Data
public class EditResult {
    public String object;
    public long created;
    public List<EditChoice> choices;
    public Usage usage;
}
