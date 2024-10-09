package com.examples.concurrent;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * <a href="https://mp.weixin.qq.com/s/DDJ42NY4knnl59UalyZPGA">CompletableFuture</a>
 */
@Slf4j
public class CompletableFutureExample {

    @Test
    void create() {
        // 默认值
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("Hi");
        // 有返回值
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Hello");
        // 无返回值
        CompletableFuture<Void> cf3 = CompletableFuture.runAsync(() -> log.info("Hi"));

        // return cf4. 主要用于callback场景，即cf2 onSuccess、onFailure后的处理
        CompletableFuture<String> cf4 = new CompletableFuture<>();
        CompletableFuture<String> cf5 = cf2.whenCompleteAsync((value, throwable) -> {
            if (throwable != null) {
                // 主动触发完成，赋异常。cf4.join时会抛异常
                cf4.completeExceptionally(throwable);
            } else {
                // 主动触发完成，赋结果值。cf4.join时会返回value值
                cf4.complete(value + " World");
            }
        });

        // 注意这两个CF值的区别。whenComplete不改变CF结果
        log.info(cf4.join());
        log.info(cf5.join());
    }

    @Test
    void then() {
        // CF完成后触发，不关心CF的返回值，无入参 & 无返回值
        CompletableFuture.supplyAsync(() -> "Hi").thenRun(() -> log.info("Hi")).thenRunAsync(() -> log.info("Hi"));

        // CF完成后触发，依赖CF的返回值，有入参 & 无返回值
        CompletableFuture.supplyAsync(() -> "Hi").thenAccept(t -> log.info(t));
        CompletableFuture.supplyAsync(() -> "Hi").thenAcceptAsync(t -> log.info(t));

        // CF完成后触发，依赖CF的返回值，有入参 & 有返回值
        CompletableFuture.supplyAsync(() -> "Hi").thenApply(t -> t + "Hello").thenApplyAsync(t -> t + "World");

        // thenCompose：连接两个CF任务，依赖上一个CF任务执行完成，但结果由第二个CF任务返回
        // thenApply：依赖上一个CF结果，再执行Function
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hi");
        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> log.info("Hello"));
        CompletableFuture<Void> cf3 = cf1.thenCompose(t -> cf2);

        // 对比thenApply的Function是CF时的返回值
        // 当产生CompletableFuture nested现象时，thenCompose能简化代码
        CompletableFuture<CompletableFuture<Void>> cf4 = cf1.thenApply(t -> cf2);
    }

    @Test
    void combineAnd() {
        // 组合上一个CF以及当前CF的结果作为入参，再执行BiFunction。有返回值
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (t, u) -> t + u)
                .thenCombineAsync(CompletableFuture.completedFuture(" !!!"), (t, u) -> t + u);
        log.info("CF1: {}", cf1.join());

        // 组合上一个CF以及当前CF的结果作为入参，再执行BiConsumer。无返回值
        CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"), (t, u) -> log.info(t + u));
        CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBothAsync(CompletableFuture.completedFuture(" World"), (t, u) -> log.info(t + u));

        // 上一个CF以及当前CF都执行完成后，再执行Runnable。无返回值
        CompletableFuture.runAsync(() -> log.info("Hello"))
                .runAfterBoth(CompletableFuture.runAsync(() -> log.info(" World")), () -> log.info(" END"));
        CompletableFuture.supplyAsync(() -> "Hello")
                .runAfterBothAsync(CompletableFuture.supplyAsync(() -> " World"), () -> log.info(" END"));

        // 等所有CF任务都执行完成后，再执行下一步
        CompletableFuture<String> cf4 = CompletableFuture.supplyAsync(() -> "Hi");
        CompletableFuture<Void> cf5 = CompletableFuture.runAsync(() -> log.info("Hello"));
        CompletableFuture<String> cf6 = CompletableFuture.completedFuture("World");

        // whenComplete的t和u是null，因为allOf返回值是CompletableFuture<Void>
        CompletableFuture.allOf(cf4, cf5, cf6)
                .whenComplete((t, u) -> log.info("END"))
                .join();
    }

    @Test
    void combineOr() {
        // 上一个CF或自身CF，只要一个执行完就可以继续下一步的Function操作。有返回值
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hi")
                .applyToEither(CompletableFuture.supplyAsync(() -> "Hello"), t -> t + "!!!");
        log.info(cf1.join());

        // 上一个CF或自身CF，只要一个执行完就可以继续下一步的Consumer操作。无返回值
        CompletableFuture<Void> cf2 = CompletableFuture.supplyAsync(() -> "Hi")
                .acceptEither(CompletableFuture.supplyAsync(() -> "Hello"), t -> log.info(t));
        cf2.join();

        // 上一个CF或自身CF，只要一个执行完就可以继续下一步的Runnable操作。无返回值
        CompletableFuture<Void> cf3 = CompletableFuture.supplyAsync(() -> "Hi")
                .runAfterEither(CompletableFuture.runAsync(() -> log.info("Hello")), () -> log.info("END"));
        cf3.join();

        // anyOf: 任意一个CF任务完成后，就执行下一步
        CompletableFuture<String> cf4 = CompletableFuture.supplyAsync(() -> "Hi");
        CompletableFuture<Void> cf5 = CompletableFuture.runAsync(() -> log.info("Hello"));
        CompletableFuture<String> cf6 = CompletableFuture.completedFuture("World");

        // whenComplete的t可能为null或者结果值，因为anyOf返回值是CompletableFuture<Object>
        CompletableFuture.anyOf(cf4, cf5, cf6)
                .whenComplete((t, u) -> log.info("END"))
                .join();
    }

    @Test
    void result() {
        // exceptionally: 捕获上一个CF的异常，并返回新值。用于异常捕获并返回默认值
        // exceptionally里不建议throw新RuntimeException异常，走whenComplete
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.1) {
                throw new RuntimeException("Oops, something was wrong!");
            }
            return "CF1";
        });
        CompletableFuture<String> cf2 = cf1.exceptionally(e -> {
            log.info("Error {}", e.getMessage());
            return "CF2";
        });
        log.info("CF2: {}", cf2.join());

        // whenComplete: 上一个CF的结果或异常后的回调执行，并返回上一个CF的结果或抛异常
        // 即cf1 onSuccess、onFailure后的处理
        CompletableFuture<String> cf3 = cf1.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.info("Error: {}", throwable.getMessage());
            } else {
                log.info("Result: {}", result);
            }
        });
        log.info("CF3: {}", cf3.join());

        // handle: 能处理上一个CF的结果或异常捕获，并返回新结果，甚至新类型结果，不抛异常
        // 通过whenComplete + complete + completeExceptionally，可以实现更丰富逻辑
        CompletableFuture<String> cf4 = cf1.handle((result, throwable) -> {
            if (throwable != null) {
                log.info("Error: {}", throwable.getMessage());
                return "CF4 error";
            } else {
                log.info("Result: {}", result);
                return result + "CF4 success";
            }
        });
        log.info("CF4: {}", cf4.join());

        // exceptionally、handle抛出异常的姿势不优雅，需要包装为RuntimeException
        CompletableFuture<String> cf5 = cf1.handle((result, throwable) -> {
            if (throwable != null) {
                log.info("Error: {}", throwable.getMessage());
                throw new RuntimeException(throwable);
            } else {
                log.info("Result: {}", result);
                return result + "CF5 success";
            }
        });
        log.info("CF5: {}", cf5.join());

        // 主动抛异常最佳实践: whenComplete + complete + completeExceptionally，实现优雅
        CompletableFuture<String> cf = new CompletableFuture<>();
        CompletableFuture.supplyAsync(Math::random).whenComplete((result, throwable) -> {
            if (throwable != null) {
                cf.completeExceptionally(throwable);
            } else {
                if (result < 0.3) {
                    cf.complete("低");
                } else if (result > 0.3 & result < 0.7) {
                    cf.complete("中");
                } else if (result > 0.9) {
                    cf.complete("高");
                } else {
                    cf.completeExceptionally(new RuntimeException("Error random"));
                }
            }
        });
        // 这里是cf.join
        log.info("CF: {}", cf.join());
    }
}
