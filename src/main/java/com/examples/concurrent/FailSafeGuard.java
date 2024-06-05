package com.examples.concurrent;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import dev.failsafe.*;
import dev.failsafe.function.CheckedSupplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FailSafeGuard {

    @Test
    public void testRetry() {
        RetryPolicy<String> retryPolicy = RetryPolicy.<String>builder()
                .handle(Exception.class)
                .handleResult(null)
                .withBackoff(1, 5, ChronoUnit.SECONDS)
                .withJitter(0.2)
                .withMaxDuration(Duration.ofSeconds(10))
                .withMaxRetries(3)
                .onRetry(e -> log.warn("Failed on attempt {}. Exception: {}", e.getAttemptCount(), e.getLastException()))
                .build();

        CheckedSupplier<String> task = () -> {
            throw new RuntimeException("Task failed");
        };

        Failsafe.with(retryPolicy).get(task);
    }

    @Test
    public void testCircuitBreaker() {

    }

    @Test
    public void testRateLimiter() {

    }

    @Test
    public void testTimeout() {
        Timeout<String> timeout = Timeout.<String>builder(Duration.ofSeconds(3))
                .withInterrupt()
                .onSuccess(event -> {
                    // 如果task内抛异常，也会返回success。event存储task返回的exception
                    log.info("Success: {}", event);
                })
                .onFailure(event -> {
                    // TimeoutExecutor.isFailure
                    // 如果result为null，或是TimeoutExceededException，则failure
                    log.error("Failure: {}", event);
                })
                .build();

        CheckedSupplier<String> supplier = () -> {
            TimeUnit.SECONDS.sleep(5);
            return "Task completed";
        };

        try {
            Failsafe.with(timeout).get(supplier);
        } catch (FailsafeException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            Throwables.throwIfUnchecked(cause);
            throw new RuntimeException("Unexpected cause from FailsafeException", cause);
        }
    }

    @Test
    public void testBulkhead() {

    }

    @Test
    public void testFallback() {
        CheckedSupplier<String> task = () -> {
            throw new RuntimeException("Remote service unavailable");
        };

        // 返回默认值
        String result1 = Failsafe.with(Fallback.of("Default")).get(task);
        log.info("Result1: {}", result1);

        // Fallback方法调用
        String result2 = Failsafe.with(Fallback.of(() -> "Fallback")).get(task);
        log.info("Result2: {}", result2);

        // 直接抛业务异常
        Failsafe.with(Fallback.ofException(e -> new UncheckedExecutionException(e.getLastException()))).get(task);
    }
}
