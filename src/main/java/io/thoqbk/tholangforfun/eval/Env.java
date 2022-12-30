package io.thoqbk.tholangforfun.eval;

import java.util.HashMap;
import java.util.Map;

public class Env {
    private Env parent;
    private Map<String, EvalResult> variables = new HashMap<>();

    public EvalResult getVariable(String name) {
        return variables.getOrDefault(name, parent == null ? new NullResult() : parent.getVariable(name));
    }

    public void setVariable(String name, EvalResult value) {
        variables.put(name, value);
    }
}
