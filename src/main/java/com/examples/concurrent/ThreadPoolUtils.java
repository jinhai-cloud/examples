package com.examples.concurrent;

import java.time.Duration;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadPoolUtils {

    public static ThreadPoolExecutor create(
            int corePoolSize, int maxPoolSize, Duration keepAlive, int queueCapacity, String threadNamePrefix) {
        BlockingQueue<Runnable> blockingQueue =
                queueCapacity <= 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueCapacity);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNamePrefix + "-%d")
                .build();

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAlive.getSeconds(),
                TimeUnit.SECONDS,
                blockingQueue,
                threadFactory,
                new AbortPolicyWithReport(threadNamePrefix));
    }

    public static ThreadPoolExecutor create(
            int maxPoolSize, Duration keepAlive, int queueCapacity, String threadNamePrefix) {
        BlockingQueue<Runnable> blockingQueue =
                queueCapacity <= 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueCapacity);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(threadNamePrefix + "-%d")
                .build();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                maxPoolSize,
                maxPoolSize,
                keepAlive.getSeconds(),
                TimeUnit.SECONDS,
                blockingQueue,
                threadFactory,
                new AbortPolicyWithReport(threadNamePrefix));
        threadPoolExecutor.allowCoreThreadTimeOut(true);

        return threadPoolExecutor;
    }
}
