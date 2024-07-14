package com.examples.concurrent;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.failsafe.*;
import dev.failsafe.function.CheckedSupplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FailsafeExample {

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
        // bursty: 令牌桶. 限制时间内的maxPermits
        // smooth: 漏桶. 限制时间内的maxRate
        // 每100ms，重置为200可用令牌数
        RateLimiter<Object> limiter = RateLimiter.burstyBuilder(200, Duration.ofMillis(100)).build();
        for (int i = 0; i < 1000; i++) {
            if (limiter.tryAcquirePermit()) {
                log.info("acquire: {}", i);
            } else {
                log.info("wait: {}", i);
                Uninterruptibles.sleepUninterruptibly(Duration.ofMillis(1));
            }
        }
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
            // 超时有些场景，需要实现ContextualSupplier，传入ExecutionContext到方法内部，实现onCancel方法
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
