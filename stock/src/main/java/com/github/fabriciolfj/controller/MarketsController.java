package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.entity.SymbolEntity;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.persistence.SymbolsRepository;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import java.util.List;

//@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
@Controller("/markets")
public class MarketsController {

    private final InMemoryStore inMemoryStore;
    private final SymbolsRepository symbolsRepository;

    @Operation(summary = "Retorna todos os produtos")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "markets")
    @Get
    public List<Symbol> all() {
        return inMemoryStore.getSymbols();
    }

    @Tag(name = "markets")
    @Get("/jpa")
    public Single<List<SymbolEntity>> allSymbolsViaJpa() {
        return Single.just(symbolsRepository.findAll());
    }
}
