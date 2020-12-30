package com.github.fabriciolfj;

import io.micronaut.context.annotation.ConfigurationInject;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@ConfigurationProperties("hello.config.greeting")
public class GreetingConfig {

    private final String br;
    private final String en;

    @ConfigurationInject
    public GreetingConfig(@NotBlank final String br, @NotBlank final String en) {
        this.br = br;
        this.en = en;
    }
}
