package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.controller.error.CustomError;
import com.github.fabriciolfj.entity.QuoteEntity;
import com.github.fabriciolfj.entity.SymbolEntity;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.persistence.QuotesRepository;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final InMemoryStore inMemoryStore;
    private final QuotesRepository quotesRepository;

    @Operation(summary = "Returns a quote for the given symbol. Fetcher from the database via Jpa")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @ApiResponse(responseCode = "400", description = "Invalid symbol specified")
    @Tag(name = "quotes")
    @Get("/{symbol}/jpa")
    public HttpResponse getQuote(@PathVariable final String symbol) {
        var quote = quotesRepository.findBySymbol(new SymbolEntity(symbol));

        if (!quote.isPresent()) {
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

    @Get("/jpa")
    public List<QuoteEntity> getAllQuotes() {
        return quotesRepository.findAll();
    }
}
