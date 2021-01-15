package com.github.fabriciolfj.dto;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Introspected //compila em tempo de execucao, fazendo a serializacao e deserializacao dos dados
public class QuoteDTO {

    private Long id;
    private BigDecimal volume;
}
