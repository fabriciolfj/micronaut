package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.controller.error.CustomError;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final InMemoryStore inMemoryStore;

    @Operation(summary = "Returns a quote for the given symbol.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
    @Tag(name = "quotes")
    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable final String symbol) {
        var quote = inMemoryStore.fetchQuote(symbol);

        if (quote.isEmpty()) {
            return HttpResponse.notFound(CustomError
                    .builder()
                    .error(HttpStatus.NOT_FOUND.name())
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .message("quote for symbol not available")
                    .path("/quotes/" + symbol)
                    .build());
        }

        return HttpResponse.ok(quote.get());
    }
}
