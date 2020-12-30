package com.github.fabriciolfj;

import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class HelloService {

    private static final Logger LOG = LoggerFactory.getLogger(HelloService.class);

    //@Value("${msg.hello}")
    @Property(name = "msg.hello", defaultValue = "Teste")
    public String greeting;

    public String hello() {
        LOG.info("Requisitando hello");
        return greeting;
    }
}
