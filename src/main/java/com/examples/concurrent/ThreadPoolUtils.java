package com.examples.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolUtils {

    public static ThreadPoolExecutor create(int coreSize, int maxSize, int queueSize, String namePrefix, boolean daemon) {
        BlockingQueue<Runnable> blockingQueue = queueSize <= 0 ?
                new SynchronousQueue<>() :
                new LinkedBlockingQueue<>(queueSize);

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
                new ThreadPoolExecutor.AbortPolicy());
    }
}
