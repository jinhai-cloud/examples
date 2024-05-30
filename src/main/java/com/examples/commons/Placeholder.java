package com.examples.commons;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;

public final class Placeholder {
    private Placeholder() {
    }

    public static String format(String template, Object... args) {
        return MessageFormatter.basicArrayFormat(template, args);
    }

    public static <V> String replace(String template, Map<String, V> map) {
        StringSubstitutor sub = new StringSubstitutor(map);
        sub.setEnableUndefinedVariableException(true);
        return sub.replace(template);
    }
}
