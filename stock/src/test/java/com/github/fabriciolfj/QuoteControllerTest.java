package com.github.fabriciolfj;

import com.github.fabriciolfj.model.Quote;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void returnQuotePerSymbol() {
        final var apple = initRandomQuote("APPLE");
        store.update(apple);

        final var appleResult = client.toBlocking().retrieve(HttpRequest.GET("/quotes/APPLE"), Quote.class);
        log.debug("Result: {}", appleResult);
        assertThat(apple).isEqualToComparingFieldByField(appleResult);
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
