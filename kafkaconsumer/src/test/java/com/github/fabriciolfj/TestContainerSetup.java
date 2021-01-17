package com.github.fabriciolfj;


import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class TestContainerSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestContainerSetup.class);
    private static final String PROPERTY_NAME = "TestContainerSetup";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    private static ApplicationContext context;

    @BeforeAll
    static void setup() {
        kafka.start();
        LOGGER.info("Bootstrap servers: {}", kafka.getBootstrapServers());

        context = ApplicationContext.run(
                CollectionUtils.mapOf("kafka.bootstrap.servers", kafka.getBootstrapServers(),
                        PROPERTY_NAME, StringUtils.TRUE),
                Environment.TEST
        );
    }

    @Test
    void consumingPriceUpdate() {
        final var testProducer = context.getBean(TestScopeExternalQuoteProduce.class);
        IntStream.range(0, 4).forEach(count -> {
            testProducer.send(new ExternalQuote("TEST" + count,
                    randomValue(ThreadLocalRandom.current()),
                    randomValue(ThreadLocalRandom.current())));
        });

        final var observer = context.getBean(PriceUpdateObserver.class);
        Awaitility.await().untilAsserted(() -> {
            assertEquals(4, observer.inspected.size());
        });
    }

    private BigDecimal randomValue(final ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(0,1000));
    }

    @KafkaListener(
            offsetReset = OffsetReset.EARLIEST,
            clientId = "external-price-observer"
    )
    @Singleton
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, version = StringUtils.TRUE)
    static class PriceUpdateObserver {
        List<PriceUpdate> inspected = new ArrayList<>();

        @Topic("price_update")
        void receive(PriceUpdate priceUpdate) {
            LOGGER.info("Consumed: {}", priceUpdate);
            inspected.add(priceUpdate);
        }
    }

    @KafkaClient(
            id = "external-quote-producer"
    )
    @Singleton
    @Requires(env = Environment.TEST)
    public interface TestScopeExternalQuoteProduce {

        @Topic("external-quotes")
        void send(final ExternalQuote quote);
    }


    @AfterAll
    static void stop() {
        kafka.stop();
    }

}
