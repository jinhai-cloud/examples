package org.cloud.examples.script;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public final class ScriptUtils {
    private static final ScriptEngine GROOVY = new ScriptEngineManager().getEngineByName("groovy");

    private ScriptUtils() {
    }

    public static Object eval(String script, Map<String, Object> map, boolean isOnce) throws ScriptException {
        Bindings bindings = GROOVY.createBindings();
        if (map != null && !map.isEmpty()) {
            bindings.putAll(map);
        }

        if (isOnce) {
            bindings.put("#jsr223.groovy.engine.keep.globals", "weak");
        }
        return GROOVY.eval(script, bindings);
    }
}
