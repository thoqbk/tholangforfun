package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.eval.BoolResult;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;
import io.thoqbk.tholangforfun.eval.NullResult;
import io.thoqbk.tholangforfun.eval.ReturnResult;

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

    @Test
    public void evalIfElseExpression() {
        String[][] tests = new String[][] {
                new String[] { "if (true) { 10 }", "10" },
                new String[] { "if (false) { 10 }", null },
                new String[] { "if (1) { 10 }", "10" },
                new String[] { "if (1 < 2) { 10 }", "10" },
                new String[] { "if (1 > 2) { 10 }", null },
                new String[] { "if (1 > 2) { 10 } else { 20 }", "20" },
                new String[] { "if (1 < 2) { 10 } else { 20 }", "10" },
        };
        for (String[] test : tests) {
            String input = test[0];
            Integer expected = test[1] == null ? null : Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            EvalResult result = new Evaluator().eval(p);
            if (expected == null) {
                result.as(NullResult.class);
            } else {
                assertEquals(expected.intValue(), result.as(IntResult.class).getValue());
            }
        }
    }

    @Test
    public void evalReturnStatement() {
        String[][] tests = new String[][] {
                new String[] { "return 10;", "10" },
                new String[] { "return 10; 9;", "10" },
                new String[] { "return 2 * 5; 9;", "10" },
                new String[] { "9; return 2 * 5; 9;", "10" },
                new String[] { "if (10 > 1) { return 10; }", "10" },
                new String[] {
                        """
                                if (10 > 1) {
                                  if (10 > 1) {
                                    return 10;
                                  }

                                  return 1;
                                }
                                """, "10" },
        };
        for (String[] test : tests) {
            String input = test[0];
            int expected = Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected,
                    new Evaluator().eval(p).as(ReturnResult.class).getValue().as(IntResult.class).getValue());
        }
    }

    @Test
    public void evalIfStatement() {
        String[][] tests = new String[][] {
                new String[] { "let a = 5; a;", "5" },
                new String[] { "let a = 5 * 5; a;", "25" },
                new String[] { "let a = 5; let b = a; b;", "5" },
                new String[] { "let a = 5; let b = a; let c = a + b + 5; c;", "15" },
        };
        for (String[] test : tests) {
            String input = test[0];
            int expected = Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(IntResult.class).getValue());
        }
    }

    @Test
    public void evalFunctionCall() {
        String[][] tests = new String[][] {
                new String[] {
                        """
                                let f = function(x) {
                                  return x;
                                  x + 10;
                                };
                                f(10);""",
                        "10",
                },
                new String[] {
                        """


                                let f = function(x) {
                                   let result = x + 10;
                                   return result;
                                   return 10;
                                };
                                f(10);""",
                        "20",
                },
                new String[] { "let identity = function(x) { x; }; identity(5);", "5" },
                new String[] { "let identity = function(x) { return x; }; identity(5);", "5" },
                new String[] { "let double = function(x) { x * 2; }; double(5);", "10" },
                new String[] { "let add = function(x, y) { x + y; }; add(5, 5);", "10" },
                new String[] { "let add = function(x, y) { x + y; }; add(5 + 5, add(5, 5));", "20" },
                new String[] { "function(x) { x; }(5)", "5" },
        };
        for (String[] test : tests) {
            String input = test[0];
            int expected = Integer.parseInt(test[1]);
            Program p = new Parser(input).parse();
            assertEquals(expected, new Evaluator().eval(p).as(IntResult.class).getValue());
        }
    }
}
