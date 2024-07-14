package com.examples.concurrent;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * EagerThreadPoolExecutor
 */
public class EagerThreadPoolExecutor extends ThreadPoolExecutor {

    public EagerThreadPoolExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            TaskQueue<Runnable> workQueue,
            ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command);

        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            // retry to offer the task into queue.
            final TaskQueue queue = (TaskQueue) super.getQueue();
            try {
                if (!queue.retryOffer(command, 0, TimeUnit.MILLISECONDS)) {
                    throw new RejectedExecutionException("Queue capacity is full.", rx);
                }
            } catch (InterruptedException x) {
                throw new RejectedExecutionException(x);
            }
        }
    }
}
