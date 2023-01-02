package io.thoqbk.tholangforfun.eval;

@FunctionalInterface
public interface BuiltIn {
    EvalResult apply(EvalResult... args);
}
