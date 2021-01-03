package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.GreetingConfig;
import com.github.fabriciolfj.HelloService;
import com.github.fabriciolfj.model.Greeting;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Status;

import javax.inject.Inject;

@Controller("${hello.controller.path:/hello}")
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

    @Get("/json")
    public Greeting toJson() {
        return new Greeting();
    }
}
