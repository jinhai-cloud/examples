package com.examples.script;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.hash.Hashing;
import com.jayway.jsonpath.JsonPath;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.transform.ThreadInterrupt;
import groovy.transform.TimedInterrupt;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.Types;

import java.nio.charset.StandardCharsets;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Groovy {
    private static final Cache<String, Class<?>> CACHE = Caffeine.newBuilder().softValues().build();
    private static final GroovyClassLoader LOADER = getClassLoader();

    private Groovy() {
    }

    public static <V> Object eval(String script, Map<String, V> map) {
        Objects.requireNonNull(script);

        Binding binding = new Binding();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach(binding::setVariable);
        }

        return InvokerHelper.createScript(getClass(script), binding).run();
    }

    private static Class<?> getClass(String script) {
        String cacheKey = Hashing.md5().hashString(script, StandardCharsets.UTF_8).toString();

        return CACHE.get(cacheKey, key -> {
            Class<?> clazz = LOADER.parseClass(script, "Script_" + cacheKey + ".groovy");
            LOADER.clearCache();
            return clazz;
        });
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
        customizers.addImports(StringUtils.class.getName(), JsonPath.class.getName());
        config.addCompilationCustomizers(customizers);

        config.addCompilationCustomizers(new ASTTransformationCustomizer(ThreadInterrupt.class));
        config.addCompilationCustomizers(new ASTTransformationCustomizer(
                Map.of("unit", TimeUnit.MILLISECONDS, "value", 1000), TimedInterrupt.class));

        //noinspection removal
        return java.security.AccessController.doPrivileged((PrivilegedAction<GroovyClassLoader>) () ->
                new GroovyClassLoader(Groovy.class.getClassLoader(), config));
    }
}
