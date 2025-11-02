package com.hoppinzq.model.openai.billing;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineItem {
    private String name;
    private BigDecimal cost;
}
