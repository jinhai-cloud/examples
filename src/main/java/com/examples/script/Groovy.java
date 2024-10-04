package com.examples.script;

import com.examples.commons.Http;
import com.examples.commons.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.hash.Hashing;
import com.jayway.jsonpath.JsonPath;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.Types;
import org.joda.time.DateTime;

import java.nio.charset.StandardCharsets;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Groovy {
    private static final Cache<String, Class<?>> CACHE = Caffeine.newBuilder().build();
    private static final GroovyClassLoader LOADER = getClassLoader();
    private static final AtomicInteger counter = new AtomicInteger(0);

    private Groovy() {
    }

    public static <V> Object eval(String name, String script, Map<String, V> map) {
        Objects.requireNonNull(script);

        Binding binding = new Binding();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach(binding::setVariable);
        }

        return InvokerHelper.createScript(getClass(name, script), binding).run();
    }

    private static Class<?> getClass(String name, String script) {
        String cacheKey = Hashing.murmur3_128().hashString(script, StandardCharsets.UTF_8).toString();

        return CACHE.get(cacheKey, key -> {
            Class<?> clazz = LOADER.parseClass(script, generateScriptName(name));
            LOADER.clearCache();
            return clazz;
        });
    }

    private static String generateScriptName(String name) {
        return "Script_" + name + "_" + counter.incrementAndGet() + ".groovy";
    }

    private static GroovyClassLoader getClassLoader() {
        SecureASTCustomizer secure = new SecureASTCustomizer();

        secure.setIndirectImportCheckEnabled(true);
        secure.setDisallowedTokens(List.of(Types.KEYWORD_WHILE, Types.KEYWORD_GOTO));
        secure.setDisallowedStatements(List.of(WhileStatement.class, DoWhileStatement.class, ForStatement.class));
        secure.setDisallowedImports(List.of(System.class.getName(), Runtime.class.getName(), Class.class.getName()));

        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(secure);

        ImportCustomizer customizers = new ImportCustomizer();
        customizers.addImports(StringUtils.class.getName(), DateTime.class.getName(),
                Http.class.getName(), JSON.class.getName(), JsonPath.class.getName());
        config.addCompilationCustomizers(customizers);

        //noinspection removal
        return java.security.AccessController.doPrivileged((PrivilegedAction<GroovyClassLoader>) () ->
                new GroovyClassLoader(Groovy.class.getClassLoader(), config));
    }
}
