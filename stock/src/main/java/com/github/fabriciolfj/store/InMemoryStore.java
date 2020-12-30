package com.github.fabriciolfj.store;

import com.github.fabriciolfj.model.Quote;
import com.github.fabriciolfj.model.Symbol;
import lombok.Getter;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Singleton
public class InMemoryStore {

    private final List<Symbol> symbols;
    private static final HashMap<String, Quote> cache = new HashMap<>();

    public InMemoryStore() {
        symbols = Stream.of("Caderno", "Lapis", "Caneta")
                .map(Symbol::new)
                .collect(Collectors.toList());

        symbols.forEach(s -> cache.put(s.getValue(), randomQuote(s)));
    }

    public Optional<Quote> fetchQuote(final String symbol) {
        return Optional.ofNullable(cache.get(symbol));
    }

    public void update(final Quote quote) {
        cache.put(quote.getSymbol().getValue(), quote);
    }

    private Quote randomQuote(Symbol s) {
        return Quote.builder()
                .symbol(s)
                .price(randomValue())
                .volume(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }
}
