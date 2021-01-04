package com.github.fabriciolfj;

import com.github.fabriciolfj.controller.WatchListController;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.model.WatchList;
import com.github.fabriciolfj.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@MicronautTest
public class WatchListControllerTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";
    @Inject
    EmbeddedApplication embeddedApplication;

    @Inject
    InMemoryAccountStore store;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void unauthorizedAccessIsForbidden() {
        try {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
            //fail("Should fail if no exception is thrown");
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
    }

    @Test
    void returnEmptyWatchListForAccount() {
        final BearerAccessRefreshToken token = getToken();

        store.deleteWatchList(TEST_ACCOUNT_ID);
        var request = HttpRequest.GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));

        final var result = client.toBlocking().retrieve("/account/watchlist", WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void updateWatchListForAccount() {
        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        final var watch  = new WatchList(symbols);

        final var result = client.toBlocking().exchange(HttpRequest.PUT("/account/watchlist", watch));
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void deleteWathlistForAccount() {
        var token = getToken();

        final var symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        final var watch  = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(symbols));
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());

        final var request = HttpRequest.DELETE("/account/watchlist/" + TEST_ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final var result = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    }

    private BearerAccessRefreshToken getToken() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());

        final BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        log.info("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());
        return token;
    }
}
