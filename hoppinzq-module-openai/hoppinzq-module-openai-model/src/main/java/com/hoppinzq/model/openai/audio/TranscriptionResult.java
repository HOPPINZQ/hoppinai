package com.hoppinzq.model.openai.audio;

import lombok.Data;

import java.util.List;

/**
 * https://platform.openai.com/docs/api-reference/audio/create
 */
@Data
public class TranscriptionResult {
    String text;
    String task;
    String language;
    Double duration;
    List<TranscriptionSegment> segments;

}
