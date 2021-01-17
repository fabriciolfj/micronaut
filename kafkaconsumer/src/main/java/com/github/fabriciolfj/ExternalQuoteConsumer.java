package com.github.fabriciolfj;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@KafkaListener(
        clientId = "mg-pricing-external-quote-consumer",
        groupId = "external-quote-consumer",
        batch = true,
        offsetReset = OffsetReset.EARLIEST
)
public class ExternalQuoteConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalQuoteConsumer.class);

    @Inject
    private PriceUpdateProducer priceUpdateProducer;

    @Topic("external-quotes")
    void receive(List<ExternalQuote> externalQuoteList) {
        LOGGER.info("Consumindo: {}", externalQuoteList);

        var prices = externalQuoteList.stream().map(c -> new PriceUpdate(c.getSymbol(), c.getLastPrice()))
                .collect(Collectors.toList());

        priceUpdateProducer.send(prices)
                .doOnError(e -> LOGGER.error("Failed produce", e.getCause()))
                .forEach(recordMetadata -> {
                    LOGGER.info("Record send to topc {} on offset {}", recordMetadata.topic(), recordMetadata.offset());
                });

    }
}
