package com.examples.mlops;

import java.util.Map;

public class GroovyBuilder<V> {
    private String script;
    private Map<String, V> map;

    public GroovyBuilder(String script, Map<String, V> map) {
        this.script = script;
        this.map = map;
    }
}
