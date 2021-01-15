package com.github.fabriciolfj.controller;

import com.github.fabriciolfj.controller.error.CustomError;
import com.github.fabriciolfj.dto.QuoteDTO;
import com.github.fabriciolfj.entity.QuoteEntity;
import com.github.fabriciolfj.entity.SymbolEntity;
import com.github.fabriciolfj.model.Symbol;
import com.github.fabriciolfj.persistence.QuotesRepository;
import com.github.fabriciolfj.store.InMemoryStore;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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


    @Get("/jpa/list/desc")
    public List<QuoteDTO> findAllDescVolume() {
        return quotesRepository.listOrderByVolumeDesc();
    }

    @Get("/jpa/filter/{volume}")
    public List<QuoteDTO> filterVolume(@PathVariable("volume") final BigDecimal volume) {
        return quotesRepository.findByVolumeGreaterThanOrderByVolumeDesc(volume);
    }

    @Get("/jpa/pagination{?page,volume}")
    public List<QuoteDTO> getPageable(@QueryValue("page")Optional<Integer> page, @QueryValue("volume") Optional<BigDecimal> volume) {
        var myPage = page.isEmpty() ? 0 : page.get();
        var myVolume = volume.isEmpty() ? BigDecimal.ZERO : volume.get();

        return quotesRepository.findByVolumeGreaterThan(myVolume, Pageable.from(myPage, 2));
    }

    @Get("/jpa/pagination/{page}")
    public List<QuoteDTO> getPageableList(@PathVariable("page") int page) {
        return quotesRepository.list(Pageable.from(page)).getContent();
    }
}
