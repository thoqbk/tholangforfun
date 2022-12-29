package io.thoqbk.tholangforfun.eval;

public class IntResult extends EvalResult {
    private final int value;

    public IntResult(int value) {
        super(ResultType.INT);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
