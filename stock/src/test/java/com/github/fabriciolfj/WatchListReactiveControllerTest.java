package com.github.fabriciolfj;

import com.github.fabriciolfj.controller.WatchListController;
import com.github.fabriciolfj.controller.WatchListReactiveController;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.model.WatchList;
import com.github.fabriciolfj.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@MicronautTest
public class WatchListReactiveControllerTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListReactiveController.ACCOUNT_ID;
    @Inject
    EmbeddedApplication embeddedApplication;

    @Inject
    InMemoryAccountStore store;

    @Inject
    @Client("/account/watchlist-reactive")
    RxHttpClient client;

    @Test
    void returnsWatchListForAccountSingle() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));

        final var result = client.toBlocking().retrieve("/single", WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }
}
