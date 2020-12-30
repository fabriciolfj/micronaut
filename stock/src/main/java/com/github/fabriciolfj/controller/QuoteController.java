package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import lombok.RequiredArgsConstructor;

@Controller("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final InMemoryStore inMemoryStore;

    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable final String symbol) {
        var quote = inMemoryStore.fetchQuote(symbol);
        return HttpResponse.ok(quote.get());
    }
}
