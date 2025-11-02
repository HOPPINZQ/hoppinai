package com.hoppinzq.model.openai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Permission {

    public String id;
    public String object;
    public long created;

    @JsonProperty("allow_create_engine")
    public boolean allowCreateEngine;

    @JsonProperty("allow_sampling")
    public boolean allowSampling;

    @JsonProperty("allow_log_probs")
    public boolean allowLogProbs;

    @JsonProperty("allow_search_indices")
    public boolean allowSearchIndices;

    @JsonProperty("allow_view")
    public boolean allowView;

    @JsonProperty("allow_fine_tuning")
    public boolean allowFineTuning;

    public String organization;

    public String group;

    @JsonProperty("is_blocking")
    public boolean isBlocking;

}
