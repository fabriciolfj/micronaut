package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.model.WatchList;
import com.github.fabriciolfj.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

//@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
@RequiredArgsConstructor
public class WatchListController {

    private final InMemoryAccountStore store;
    public final static UUID ACCOUNT_ID = UUID.randomUUID();

    @Get(processes = MediaType.APPLICATION_JSON)
    public WatchList get() {
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update(WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(value = "/{accountId}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public void delete(final UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
