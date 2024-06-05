package com.examples.script;

import pemja.core.PythonInterpreter;
import pemja.core.PythonInterpreterConfig;

import java.util.Objects;

public class Python {
    private static final PythonInterpreterConfig config = PythonInterpreterConfig
            .newBuilder()
            .setPythonExec("python3") // specify python exec
            .addPythonPaths("pythonPath") // add path to search path
            .build();

    public static <T> T call(String script, Class<T> clazz) {
        try (PythonInterpreter interpreter = new PythonInterpreter(config)) {
            interpreter.exec(script);
            Object result = interpreter.invoke("call");

            return Objects.nonNull(result) ? clazz.cast(result) : null;
        } catch (Exception e) {
            throw new IllegalCallerException(e);
        }
    }
}
