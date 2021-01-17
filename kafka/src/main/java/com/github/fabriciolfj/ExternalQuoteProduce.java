package com.github.fabriciolfj;

import com.github.fabriciolfj.external.ExternalQuote;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient(
        id = "external-quote-producer"
)
public interface ExternalQuoteProduce {

    @Topic("external-quotes")
    void send(@KafkaKey final String symbol, final ExternalQuote quote);
}
