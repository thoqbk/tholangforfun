package io.thoqbk.tholangforfun.eval;

import java.util.HashMap;
import java.util.Map;

import io.thoqbk.tholangforfun.exceptions.EvalException;

public class Env {
    private Env parent;
    private Map<String, EvalResult> variables = new HashMap<>();

    public Env() {
        this(null);
    }

    public Env(Env parent) {
        this.parent = parent;
    }

    public EvalResult getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.getVariable(name);
        }
        return null;
    }

    public void setVariable(String name, EvalResult value) {
        variables.put(name, value);
    }

    public void updateVariable(String name, EvalResult value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
            return;
        }
        if (parent != null) {
            parent.updateVariable(name, value);
            return;
        }
        throw new EvalException(String.format("Cannot update variable '%s' before it's defined", name));
    }
}
