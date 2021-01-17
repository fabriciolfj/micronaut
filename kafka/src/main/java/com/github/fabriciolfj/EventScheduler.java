package com.github.fabriciolfj;

import com.github.fabriciolfj.external.ExternalQuote;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@Requires(notEnv = Environment.TEST)
public class EventScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventScheduler.class);
    private static final List<String> SYMBOLS = Arrays.asList("APPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX");
    private final ExternalQuoteProduce externalQuoteProduce;

    public EventScheduler(final ExternalQuoteProduce externalQuoteProduce) {
        this.externalQuoteProduce = externalQuoteProduce;
    }

    @Scheduled(fixedDelay = "10s")
    void generate() {
        final ThreadLocalRandom random =ThreadLocalRandom.current();
        final var externalQuote = new ExternalQuote(
                SYMBOLS.get(random.nextInt(0, SYMBOLS.size() - 1)),
                randomValue(random),
                randomValue(random)
        );

        LOGGER.info("Generate external quote: {}", externalQuote);
        externalQuoteProduce.send(externalQuote.getSymbol(), externalQuote);
    }

    private BigDecimal randomValue(final ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(0, 1000));
    }
}
