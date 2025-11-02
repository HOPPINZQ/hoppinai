package com.hoppinzq.model.openai.billing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DailyCost {
    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("line_items")
    private List<LineItem> lineItems;
}
