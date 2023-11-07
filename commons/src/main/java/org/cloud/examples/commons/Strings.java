package org.cloud.examples.commons;

import org.slf4j.helpers.MessageFormatter;

public final class Strings {
    private Strings() {
    }

    public static String format(String template, Object... args) {
        return MessageFormatter.basicArrayFormat(template, args);
    }
}
