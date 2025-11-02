package com.hoppinzq.model.openai.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/moderations/create
 */
@Data
public class Moderation {
    public boolean flagged;
    public ModerationCategories categories;
    @JsonProperty("category_scores")
    public ModerationCategoryScores categoryScores;
}
