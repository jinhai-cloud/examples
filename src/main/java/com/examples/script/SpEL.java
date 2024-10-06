package com.examples.script;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.collections4.MapUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.Objects;

public class SpEL {
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final Cache<ScriptKey, Expression> CACHE = Caffeine.newBuilder().build();

    public static <V> Object eval(String script, Map<String, V> map) {
        Objects.requireNonNull(script);

        EvaluationContext context = new StandardEvaluationContext();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach(context::setVariable);
        }

        ScriptKey scriptKey = new ScriptKey(script);
        Expression expression = CACHE.get(scriptKey, key -> PARSER.parseExpression(script));
        return expression.getValue(context);
    }
}
