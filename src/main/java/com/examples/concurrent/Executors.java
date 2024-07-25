package com.examples.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static com.google.common.base.Throwables.throwIfUnchecked;

public class Executors {
    private Executors() {
    }

    public static <T> void executeUntilFailure(Collection<Callable<T>> tasks, Executor executor) {
        CompletionService<T> completionService = new ExecutorCompletionService<>(executor);
        List<Future<T>> futures = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            futures.add(completionService.submit(task));
        }

        try {
            for (int i = 0; i < futures.size(); i++) {
                getDone(take(completionService));
            }
        } catch (Exception failure) {
            try {
                futures.forEach(future -> future.cancel(true));
            } catch (RuntimeException e) {
                failure.addSuppressed(e);
            }
            throw failure;
        }
    }

    private static <T> Future<T> take(CompletionService<T> completionService) {
        try {
            return completionService.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }

    private static <T> T getDone(Future<T> future) {
        Objects.requireNonNull(future, "future is null");

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Throwable cause = e.getCause() == null ? e : e.getCause();
            throwIfUnchecked(cause);
            throw new RuntimeException(cause);
        }
    }
}
