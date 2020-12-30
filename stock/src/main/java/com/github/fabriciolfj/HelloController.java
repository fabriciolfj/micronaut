package com.github.fabriciolfj;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Status;

import javax.inject.Inject;

@Controller("{hello.controller.path}")
public class HelloController {

    @Inject
    private HelloService helloService;

    @Inject
    private GreetingConfig greetingConfig;

    @Get
    @Status(HttpStatus.ACCEPTED)
    public String get() {
        return helloService.hello();
    }

    @Get("/br")
    public String getBr() {
        return greetingConfig.getBr();
    }

    @Get("/en")
    public String getEn() {
        return greetingConfig.getEn();
    }
}
