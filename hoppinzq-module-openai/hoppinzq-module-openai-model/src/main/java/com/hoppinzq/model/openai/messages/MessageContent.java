package com.hoppinzq.model.openai.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoppinzq.model.openai.messages.content.ImageFile;
import com.hoppinzq.model.openai.messages.content.Text;
import lombok.Data;


/**
 * https://platform.openai.com/docs/api-reference/messages/object
 */
@Data
public class MessageContent {
    String type;
    Text text;
    @JsonProperty("image_file")
    ImageFile imageFile;
}
