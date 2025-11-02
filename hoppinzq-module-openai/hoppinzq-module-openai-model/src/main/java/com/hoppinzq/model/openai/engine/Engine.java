package com.hoppinzq.model.openai.engine;

import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/retrieve-engine
 */
@Deprecated
@Data
public class Engine {
    public String id;
    public String object;
    public String owner;
    public boolean ready;
}
