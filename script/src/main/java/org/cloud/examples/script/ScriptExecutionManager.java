package org.cloud.examples.script;

import javax.script.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptExecutionManager {

    private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    public void load(String scriptId, String script) {
        ScriptEngine engine = scriptEngineManager.getEngineByName("groovy");
        try {
            CompiledScript compiledScript = ((Compilable) engine).compile(script);
            compiledScripts.put(scriptId, compiledScript);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public Object eval(String scriptId, Map<String, Object> map) throws ScriptException {
        CompiledScript compiledScript = compiledScripts.get(scriptId);
        if (compiledScript == null) {
            throw new RuntimeException();
        }

        Bindings bindings = new SimpleBindings(map);
        return compiledScript.eval(bindings);
    }
}
