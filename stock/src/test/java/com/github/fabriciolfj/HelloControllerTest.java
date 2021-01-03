package com.github.fabriciolfj;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@MicronautTest
public class HelloControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHelloResponse() {
        final String result = client.toBlocking().retrieve("/hello");
        assertEquals("Hello world", result);
    }

    @Test
    void testHelloBr() {
        final String result = client.toBlocking().retrieve("/hello/br");
        assertEquals("Ola", result);
    }

    @Test
    void testHelloEn() {
        final String result = client.toBlocking().retrieve("/hello/en");
        assertEquals(result, "Hello");
    }

    @Test
    void testGreetingAsJson() {
        final ObjectNode result = client.toBlocking().retrieve("/hello/json", ObjectNode.class);
        log.info(result.toString());
    }
}
