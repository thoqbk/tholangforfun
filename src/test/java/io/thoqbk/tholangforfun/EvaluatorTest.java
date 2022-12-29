package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.eval.IntResult;

public class EvaluatorTest {
    @Test
    public void evalReturnsResultOfIntExpression() {
        String[][] tests = new String[][] {
                new String[] { "5;", "5" },
                new String[] { "10", "10" },
                new String[] { "-5", "-5" },
                new String[] { "--5", "5" },
        };
        for (String[] test : tests) {
            String input = test[0];
            int expected = Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(IntResult.class).getValue());
        }
    }
}
