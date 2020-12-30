package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@Controller("/markets")
public class MarketsController {

    private final InMemoryStore inMemoryStore;

    @Get
    public List<Symbol> all() {
        return inMemoryStore.getSymbols();
    }
}
