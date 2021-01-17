package com.github.fabriciolfj;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@KafkaListener(
        clientId = "mg-pricing-external-quote-consumer",
        groupId = "external-quote-consumer",
        batch = true,
        offsetReset = OffsetReset.EARLIEST
)
public class ExternalQuoteConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalQuoteConsumer.class);

    @Topic("external-quotes")
    void receive(List<ExternalQuote> externalQuoteList) {
        LOGGER.info("Consumindo: {}", externalQuoteList);
    }
}
