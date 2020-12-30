package com.github.fabriciolfj.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quote {

    private Symbol symbol;
    private BigDecimal price;
    private BigDecimal volume;
}
