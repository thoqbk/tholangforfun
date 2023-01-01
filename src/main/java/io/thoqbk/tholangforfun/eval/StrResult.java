package io.thoqbk.tholangforfun.eval;

public class StrResult extends EvalResult {
    private String value;

    public StrResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
