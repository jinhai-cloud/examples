package com.examples.script;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.hash.Hashing;
import org.apache.commons.collections4.MapUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class SpEL {
    private static final ExpressionParser PARSER = new SpelExpressionParser(
            new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, SpEL.class.getClassLoader()));
    private static final Cache<String, Expression> CACHE = Caffeine.newBuilder().build();

    public static <V> Object eval(String script, Map<String, V> map) {
        Objects.requireNonNull(script);

        EvaluationContext context = new StandardEvaluationContext();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach(context::setVariable);
        }

        String cacheKey = Hashing.murmur3_128().hashString(script, StandardCharsets.UTF_8).toString();
        Expression expression = CACHE.get(cacheKey, key -> PARSER.parseExpression(script));
        return expression.getValue(context);
    }
}
