package com.github.fabriciolfj;

import com.github.fabriciolfj.controller.WatchListController;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.model.WatchList;
import com.github.fabriciolfj.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@MicronautTest
public class WatchListControllerTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;
    @Inject
    EmbeddedApplication embeddedApplication;

    @Inject
    InMemoryAccountStore store;

    @Inject
    @Client("/account/watchlist")
    RxHttpClient client;

    @Test
    void returnEmptyWatchListForAccount() {
        store.deleteWatchList(TEST_ACCOUNT_ID);
        final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));

        final var result = client.toBlocking().retrieve("/", WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void updateWatchListForAccount() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        final var watch  = new WatchList(symbols);

        final var result = client.toBlocking().exchange(HttpRequest.PUT("/", watch));
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void deleteWathlistForAccount() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        final var watch  = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());

        final var result = client.toBlocking().exchange(HttpRequest.DELETE("/" + TEST_ACCOUNT_ID));
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    }
}
