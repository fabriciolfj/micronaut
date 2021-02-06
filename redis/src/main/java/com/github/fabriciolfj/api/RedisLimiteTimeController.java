package com.github.fabriciolfj.api;

import io.lettuce.core.RedisAsyncCommandsImpl;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalTime;
import java.util.concurrent.ExecutionException;

@Controller("/time")
public class RedisLimiteTimeController {

    private static final Logger LOG = LoggerFactory.getLogger(RedisLimiteTimeController.class);
    private static final int QUOTA_PER_MINUTE = 10;
    private StatefulRedisConnection<String, String> redis;
    RedisAsyncCommands<String, String> commands;

    public RedisLimiteTimeController(StatefulRedisConnection<String, String> redis) {
        this.redis = redis;
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/async")
    public void asyncGet() throws ExecutionException, InterruptedException {
        LOG.info(getString());
    }

    @Get()
    public String time() throws ExecutionException, InterruptedException {
        return getString();
    }

    private String getString() throws InterruptedException, ExecutionException {
        final String key = "EXAMPLE::TIME";
        final String value = redis.sync().get(key);
        int currentQuota = value == null ? 0 : Integer.parseInt(value);

        if (currentQuota >= QUOTA_PER_MINUTE) {
            final String err = String.format("Rate limite reached %s %s/%s", key, currentQuota, QUOTA_PER_MINUTE);
            LOG.error(err);
            return err;
        }

        LOG.info("Current quota {}, in {}/{}", key, currentQuota, QUOTA_PER_MINUTE);
        increaseCurrentQuota(key);
        LOG.info(commands.get("Fabricio").get());
        return LocalTime.now().toString();
    }

    private void increaseCurrentQuota(final String key) {
        commands = redis.async();
        commands.multi();
        commands.set("Fabricio", "30");
        commands.incrby(key, 1);

        var remainingSeconds = 60 - LocalTime.now().getSecond();
        commands.expire(key, remainingSeconds);
        commands.exec();
    }
}
