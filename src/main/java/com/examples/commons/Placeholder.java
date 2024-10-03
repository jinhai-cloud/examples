package com.examples.commons;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;

public final class Placeholder {

    private static final PropertyPlaceholderHelper helper =
            new PropertyPlaceholderHelper("${", "}");

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

    public static String resolve(String template, Map<String, String> map) {
        return helper.replacePlaceholders(template, map::get);
    }
}
