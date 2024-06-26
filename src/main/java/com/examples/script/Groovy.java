package com.examples.script;

import org.apache.commons.collections4.MapUtils;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public final class Groovy {
    private static final ScriptEngine GROOVY = new ScriptEngineManager().getEngineByName("groovy");

    private Groovy() {
    }

    public static <V> Object eval(String script, Map<String, V> map, boolean once) throws ScriptException {
        Bindings bindings = GROOVY.createBindings();
        if (MapUtils.isNotEmpty(map)) {
            bindings.putAll(map);
        }

        if (once) {
            bindings.put("#jsr223.groovy.engine.keep.globals", "weak");
        }
        return GROOVY.eval(script, bindings);
    }
}
