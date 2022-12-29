package io.thoqbk.tholangforfun.eval;

public abstract class EvalResult {
    public <T extends EvalResult> T as(Class<T> clazz) {
        return clazz.cast(this);
    }

    public <T extends EvalResult> boolean is(Class<T> clazz) {
        return clazz.isInstance(this);
    }
}
