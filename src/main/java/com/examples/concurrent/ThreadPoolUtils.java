package com.examples.concurrent;

import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadPoolUtils {

    public static ThreadPoolExecutor create(
            int coreSize, int maxSize, int queueSize, String namePrefix, boolean daemon) {
        BlockingQueue<Runnable> blockingQueue =
                queueSize <= 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueSize);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(namePrefix + "-%d")
                .setDaemon(daemon)
                .build();

        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60,
                TimeUnit.SECONDS,
                blockingQueue,
                threadFactory,
                new AbortPolicyWithReport(namePrefix));
    }

    public static ThreadPoolExecutor createEagerPool(
            int coreSize, int maxSize, int queueSize, String namePrefix, boolean daemon) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(namePrefix + "-%d")
                .setDaemon(daemon)
                .build();

        TaskQueue<Runnable> taskQueue = new TaskQueue<>(queueSize <= 0 ? 1 : queueSize);
        EagerThreadPoolExecutor executor = new EagerThreadPoolExecutor(
                coreSize,
                maxSize,
                60,
                TimeUnit.SECONDS,
                taskQueue,
                threadFactory,
                new AbortPolicyWithReport(namePrefix));
        taskQueue.setExecutor(executor);
        return executor;
    }
}
