package com.examples.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompletableFutureExample {

    @Test
    public void create() {
        // 默认值
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("Hi");
        // 有返回值
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Hi");
        // 无返回值
        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> log.info("Hi"));

        // return cf4. 主要用于callback场景，即cf2 onSuccess、onFailure后的处理
        CompletableFuture<String> cf4 = new CompletableFuture<>();
        cf2.whenCompleteAsync((value, throwable) -> {
            if (throwable != null) {
                cf4.completeExceptionally(throwable);
            } else {
                cf4.complete(value);
            }
        });
    }

    @Test
    public void then() {
        // 不关心上一个CF的返回值，无入参 & 无返回值
        CompletableFuture.supplyAsync(() -> "Hi")
                .thenRun(() -> log.info("Hi"))
                .thenRunAsync(() -> log.info("Hi"))
                .thenRunAsync(() -> log.info("Hi"), MoreExecutors.directExecutor());


    }
}
