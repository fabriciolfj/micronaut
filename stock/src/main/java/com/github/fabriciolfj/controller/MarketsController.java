package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
@Controller("/markets")
public class MarketsController {

    private final InMemoryStore inMemoryStore;

    @Operation(summary = "Retorna todos os produtos")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "markets")
    @Get
    public List<Symbol> all() {
        return inMemoryStore.getSymbols();
    }
}
