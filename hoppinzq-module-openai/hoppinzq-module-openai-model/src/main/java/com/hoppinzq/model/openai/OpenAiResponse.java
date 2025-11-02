package com.hoppinzq.model.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse<T> {
    public List<T> data;
    public String object;
    @JsonProperty("first_id")
    public String firstId;
    @JsonProperty("last_id")
    public String lastId;
    @JsonProperty("has_more")
    public boolean hasMore;
}
