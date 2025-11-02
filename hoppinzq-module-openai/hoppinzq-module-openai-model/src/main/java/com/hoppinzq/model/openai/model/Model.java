package com.hoppinzq.model.openai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 模型详情
 * https://beta.openai.com/docs/api-reference/models
 */
@Data
public class Model {
    public String id;
    public String object;
    @JsonProperty("owned_by")
    public String ownedBy;
    @Deprecated
    public List<Permission> permission;
    public String root;
    public String parent;
}
