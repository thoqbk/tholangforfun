package io.thoqbk.tholangforfun.eval;

public abstract class EvalResult {
    private ResultType type;

    public EvalResult(ResultType type) {
        this.type = type;
    }

    public ResultType getType() {
        return type;
    }

    public <T extends EvalResult> T as(Class<T> clazz) {
        return clazz.cast(this);
    }
}
