package com.examples.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolUtils {

    public ThreadPoolExecutor create(int coreSize, int maxSize, int queueSize, String namePrefix) {
        Preconditions.checkArgument(queueSize >= 0, "Wrong queue length");

        BlockingQueue<Runnable> blockingQueue = queueSize == 0 ?
                new SynchronousQueue<>() :
                new LinkedBlockingQueue<>(queueSize);

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(namePrefix + "-%d")
                .setDaemon(true)
                .build();

        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                300,
                TimeUnit.SECONDS,
                blockingQueue,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }
}
