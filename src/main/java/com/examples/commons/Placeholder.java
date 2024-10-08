package com.examples.commons;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public final class Placeholder {

    private static final PropertyPlaceholderHelper helper =
            new PropertyPlaceholderHelper("${", "}");
    private static final MustacheFactory MUSTACHE = new DefaultMustacheFactory();

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

    public static <V> String apply(String template, Map<String, V> data) {
        Mustache mustache = MUSTACHE.compile(new StringReader(template), "template");
        return mustache.execute(new StringWriter(), data).toString();
    }
}
