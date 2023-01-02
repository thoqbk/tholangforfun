package io.thoqbk.tholangforfun;

import java.util.HashMap;
import java.util.Map;

import io.thoqbk.tholangforfun.eval.BuiltIn;
import io.thoqbk.tholangforfun.eval.BuiltInResult;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;
import io.thoqbk.tholangforfun.eval.StrResult;
import io.thoqbk.tholangforfun.exceptions.EvalException;

public class BuiltIns {
    private static Map<String, BuiltIn> functions = new HashMap<>() {
        {
            put("len", BuiltIns::len);
        }
    };

    public static BuiltInResult get(String name) {
        return new BuiltInResult(functions.get(name));
    }

    private static EvalResult len(EvalResult... args) {
        if (args.length != 1) {
            throw new EvalException("Number of arguments must be 1, got " + args.length);
        }
        if (!args[0].is(StrResult.class)) {
            throw new EvalException("len is only applied for string data type");
        }
        return new IntResult(args[0].as(StrResult.class).getValue().length());
    }
}
