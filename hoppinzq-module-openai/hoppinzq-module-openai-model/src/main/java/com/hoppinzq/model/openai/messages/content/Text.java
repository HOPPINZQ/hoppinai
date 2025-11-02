package com.hoppinzq.model.openai.messages.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Text {
    String value;
    List<Annotation> annotations;
}
