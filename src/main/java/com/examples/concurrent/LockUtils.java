package com.examples.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

@Slf4j
public class LockUtils {
    public static final int DEFAULT_TIMEOUT = 60_000;

    public static void safeLock(Lock lock, int timeout, Runnable runnable) {
        try {
            if (!lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                log.error("Try to lock failed, timeout: " + timeout, new TimeoutException());
            }
            runnable.run();
        } catch (InterruptedException e) {
            log.warn("Try to lock failed", e);
            Thread.currentThread().interrupt();
        } finally {
            try {
                lock.unlock();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
