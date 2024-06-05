package com.examples.commons;

import java.time.Duration;
import java.time.Instant;

public class TimeUtils {

    public static Instant now() {
        return Instant.now();
    }

    public static long nowMs() {
        return System.currentTimeMillis();
    }

    public static long diffMs(long startMs) {
        return System.currentTimeMillis() - startMs;
    }

    public static long diffMs(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }
}
