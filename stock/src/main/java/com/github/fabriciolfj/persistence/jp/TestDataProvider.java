package com.github.fabriciolfj.persistence.jp;

import com.github.fabriciolfj.entity.QuoteEntity;
import com.github.fabriciolfj.entity.SymbolEntity;
import com.github.fabriciolfj.model.Quote;
import com.github.fabriciolfj.persistence.QuotesRepository;
import com.github.fabriciolfj.persistence.SymbolsRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.Stream;

@Singleton
@RequiredArgsConstructor
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final Random RANDOM = new Random();

    private final SymbolsRepository repository;
    private final QuotesRepository quotesRepository;

    @EventListener
    public void init(StartupEvent event) {
        if (repository.findAll().isEmpty()) {
            Stream.of("APPL", "AMZN", "FB", "TSLA")
                    .map(SymbolEntity::new)
                    .forEach(repository::save);
        }

        //if (quotesRepository.findAll().isEmpty()) {
            repository.findAll().forEach(symbol -> {
                var quote = new QuoteEntity();
                quote.setSymbol(symbol);
                quote.setAsk(randomValue());
                quote.setVolume(randomValue());
                quote.setLastPrice(randomValue());
                quote.setBid(randomValue());
                quotesRepository.save(quote);
            });
        //}
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble());
    }
}
