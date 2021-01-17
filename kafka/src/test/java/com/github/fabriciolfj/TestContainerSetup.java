package com.github.fabriciolfj;


import com.github.fabriciolfj.external.ExternalQuote;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import org.awaitility.Awaitility;
import org.junit.After;
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
    void producing10RecordsWorks() {
        final var producer = context.getBean(ExternalQuoteProduce.class);
        IntStream.range(0, 10)
                .forEach(count -> {
                    final ExternalQuote quote = new ExternalQuote("SYMBOL" + count,
                            randomValue(ThreadLocalRandom.current()),
                            randomValue(ThreadLocalRandom.current()));
                    producer.send("SYMBOL" + count, quote);
                });

        final var observer = context.getBean(ExternalQuoteObserver.class);
        Awaitility.await().untilAsserted(() -> {
            assertEquals(10, observer.inspected.size());
        });
    }

    private BigDecimal randomValue(final ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(0,1000));
    }

    @KafkaListener(
            offsetReset = OffsetReset.EARLIEST
    )
    @Singleton
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, version = StringUtils.TRUE)
    static class ExternalQuoteObserver {
        List<ExternalQuote> inspected = new ArrayList<>();

        @Topic("external-quotes")
        void receive(ExternalQuote externalQuote) {
            LOGGER.info("Consumed: {}", externalQuote);
            inspected.add(externalQuote);
        }
    }

    @AfterAll
    static void stop() {
        kafka.stop();
    }

}
