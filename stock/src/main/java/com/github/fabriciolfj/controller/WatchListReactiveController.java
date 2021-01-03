package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.model.WatchList;
import com.github.fabriciolfj.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Named;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@Controller("/account/watchlist-reactive")
public class WatchListReactiveController {

    private final InMemoryAccountStore store;
    private final Scheduler scheduler;
    public final static UUID ACCOUNT_ID = UUID.randomUUID();

    public WatchListReactiveController(@Named(TaskExecutors.IO) final ExecutorService executorService, final InMemoryAccountStore store) {
        this.store = store;
        this.scheduler = Schedulers.from(executorService);
    }

    @Get(value = "/single", produces = MediaType.APPLICATION_JSON)
    public Single<WatchList> getAsSingle() {
        return Single.fromCallable(() -> {
            log.info("getSingle() - {}", Thread.currentThread().getName());
            return store.getWatchList(ACCOUNT_ID);
        }).subscribeOn(this.scheduler);
    }

    @Get(processes = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get() {
        log.info("Thread: {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update(WatchList watchList) {
        log.info("Thread: {}", Thread.currentThread().getName());
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(value = "/{accountId}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public void delete(final UUID accountId) {
        log.info("Thread: {}", Thread.currentThread().getName());
        store.deleteWatchList(accountId);
    }
}
