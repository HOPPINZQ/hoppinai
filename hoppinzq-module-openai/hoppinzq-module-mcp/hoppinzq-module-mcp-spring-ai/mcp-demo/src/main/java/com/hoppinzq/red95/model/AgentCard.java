package com.hoppinzq.red95.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AgentCard represents the metadata card for an agent
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCard {
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("url")
    String url;
    @JsonProperty("version")
    String version;
    @JsonProperty("documentationUrl")
    String d_url;


}
