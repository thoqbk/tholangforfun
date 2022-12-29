package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.eval.BoolResult;
import io.thoqbk.tholangforfun.eval.IntResult;

public class EvaluatorTest {
    @Test
    public void evalReturnsResultOfIntExpression() {
        String[][] tests = new String[][] {
                new String[] { "5;", "5" },
                new String[] { "10", "10" },
                new String[] { "-5", "-5" },
                new String[] { "--5", "5" },
                new String[] { "5 + 5 + 5 + 5 - 10", "10" },
                new String[] { "2 * 2 * 2 * 2 * 2", "32" },
                new String[] { "-50 + 100 + -50", "0" },
                new String[] { "5 * 2 + 10", "20" },
                new String[] { "5 + 2 * 10", "25" },
                new String[] { "20 + 2 * -10", "0" },
                new String[] { "50 / 2 * 2 + 10", "60" },
                new String[] { "2 * (5 + 10)", "30" },
                new String[] { "3 * 3 * 3 + 10", "37" },
                new String[] { "3 * (3 * 3) + 10", "37" },
                new String[] { "(5 + 10 * 2 + 15 / 3) * 2 + -10", "50" },
        };
        for (String[] test : tests) {
            String input = test[0];
            int expected = Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(IntResult.class).getValue());
        }
    }

    @Test
    public void evalReturnsResultOfBoolExpression() {
        String[][] tests = new String[][] {
                new String[] { "true", "true" },
                new String[] { "false", "false" },
                new String[] { "1 < 2", "true" },
                new String[] { "1 > 2", "false" },
                new String[] { "1 < 1", "false" },
                new String[] { "1 > 1", "false" },
                new String[] { "1 == 1", "true" },
                new String[] { "1 != 1", "false" },
                new String[] { "1 == 2", "false" },
                new String[] { "1 != 2", "true" },
                new String[] { "true == true", "true" },
                new String[] { "false == false", "true" },
                new String[] { "true == false", "false" },
                new String[] { "true != false", "true" },
                new String[] { "false != true", "true" },
                new String[] { "(1 < 2) == true", "true" },
                new String[] { "(1 < 2) == false", "false" },
                new String[] { "(1 > 2) == true", "false" },
                new String[] { "(1 > 2) == false", "true" },
        };
        for (String[] test : tests) {
            String input = test[0];
            boolean expected = Boolean.parseBoolean(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(BoolResult.class).getValue());
        }
    }

    @Test
    public void evalBangOperatorShouldReturnCorrectResult() {
        String[][] tests = new String[][] {
                new String[] { "!true", "false" },
                new String[] { "!false", "true" },
                new String[] { "!5", "false" },
                new String[] { "!!true", "true" },
                new String[] { "!!false", "false" },
                new String[] { "!!5", "true" },
        };
        for (String[] test : tests) {
            String input = test[0];
            boolean expected = Boolean.parseBoolean(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(BoolResult.class).getValue());
        }
    }
}
