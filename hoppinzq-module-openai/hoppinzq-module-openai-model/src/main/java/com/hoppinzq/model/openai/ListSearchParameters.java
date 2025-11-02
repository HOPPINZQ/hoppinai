package com.hoppinzq.model.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ListSearchParameters {
    private Integer limit;
    private Order order;
    private String after;
    private String before;

    public enum Order {
        @JsonProperty("asc")
        ASCENDING,
        @JsonProperty("desc")
        DESCENDING
    }
}
