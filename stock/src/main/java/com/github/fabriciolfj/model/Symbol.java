package com.github.fabriciolfj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "Symbol", description = "Abreviacao dos produtos no estoque")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {

    @Schema(description = "symbol value", minLength = 1, maxLength = 20)
    private String value;
}
