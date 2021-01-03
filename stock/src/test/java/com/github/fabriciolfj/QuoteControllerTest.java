package com.github.fabriciolfj;

import com.github.fabriciolfj.controller.error.CustomError;
import com.github.fabriciolfj.model.Quote;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MicronautTest
public class QuoteControllerTest {

    @Inject
    EmbeddedApplication embeddedApplication;

    @Inject
    InMemoryStore store;

    @Inject
    @Client("/")
    RxHttpClient client;

    //@Test
    void returnQuotePerSymbol() {
        final var apple = initRandomQuote("APPLE");
        store.update(apple);

        final var appleResult = client.toBlocking().retrieve(HttpRequest.GET("/quotes/APPLE"), Quote.class);
        log.debug("Result: {}", appleResult);
        assertThat(apple).isEqualToComparingFieldByField(appleResult);
    }

    @Test
    void returnNotFoundOnUnsupportedSymbol() {
        try {
            client.toBlocking().retrieve(HttpRequest.GET("/quotes/UNSUPPORTED"), Argument.of(Quote.class), Argument.of(CustomError.class));
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            final Optional<CustomError> customError = e.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("quote for symbol not available", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());
        }
    }

    private Quote initRandomQuote(final String symbol) {
        return Quote.builder()
                .symbol(new Symbol(symbol))
                .volume(randomValue())
                .price(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }
}
