package com.examples.script;

import java.util.Objects;

public class ScriptKey {
    private final String script;

    public ScriptKey(String script) {
        this.script = script;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptKey scriptKey = (ScriptKey) o;
        return Objects.equals(script, scriptKey.script);
    }

    @Override
    public int hashCode() {
        return Objects.hash(script);
    }
}
