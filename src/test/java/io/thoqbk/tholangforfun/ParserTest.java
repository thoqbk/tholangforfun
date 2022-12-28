package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Function;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.If;
import io.thoqbk.tholangforfun.ast.statements.Let;
import io.thoqbk.tholangforfun.ast.statements.Return;
import io.thoqbk.tholangforfun.ast.statements.Statement;

public class ParserTest {
    @Test
    public void parseLetStatementShouldReturnCorrectIdentifiers() {
        String input = """
                let x = 1;
                let abc = 10000;
                """;
        List<Statement> statements = new Parser(input).parse();
        assertEquals(statements.size(), 2);
        assertEquals(statements.get(0).getToken().getType(), TokenType.LET);

        Let let1 = statements.get(0).as(Let.class);
        assertEquals(let1.getVariableName(), "x");

        Let let2 = statements.get(1).as(Let.class);
        assertEquals(let2.getVariableName(), "abc");
    }

    @Test
    public void parseReturnStatementShouldReturnCorrectToken() {
        String input = "return 10;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(statements.size(), 1);
        assertEquals(statements.get(0).as(Return.class).getToken().getType(), TokenType.RETURN);
    }

    @Test
    public void parseExpressionShouldReturnCorrectIdentifier() {
        String input = "return foobar;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(statements.size(), 1);
        Return stm = statements.get(0).as(Return.class);
        assertEquals(TokenType.IDENT, stm.getValue().getToken().getType());
        assertEquals("foobar", stm.getValue().getToken().getLiteral());
    }

    @Test
    public void parsePrefixMinusShouldReturnPrefixExpression() {
        String input = "let abc = -1000;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        Prefix prefix = statements.get(0).as(Let.class).getExpression().as(Prefix.class);
        assertEquals(TokenType.MINUS, prefix.getToken().getType());
        Int intExpression = prefix.getRight().as(Int.class);
        assertEquals(1000, intExpression.getValue());
    }

    @Test
    public void parseInfixShouldReturnLeftAndRight() {
        String input = "return 100 + 200;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        Infix infix = statements.get(0).as(Return.class).getValue().as(Infix.class);
        assertEquals(TokenType.PLUS, infix.getToken().getType());

        Int left = infix.getLeft().as(Int.class);
        Int right = infix.getRight().as(Int.class);
        assertEquals(100, left.getValue());
        assertEquals(200, right.getValue());
    }

    @Test
    public void parseExpressionShouldConsiderPrecedences() {
        String[][] tests = new String[][] {
                new String[] { "a + b", "(a + b)" },
                new String[] { "a + b;c + d", "(a + b)(c + d)" },
                new String[] { "a + b / 2;", "(a + (b / 2))" },
                new String[] { "a + b + c;", "((a + b) + c)" },
                new String[] { "100 * 2 + 1;", "((100 * 2) + 1)" },
                new String[] { "a + b * c - d / 2;", "((a + (b * c)) - (d / 2))" },
                new String[] { "-a * b;", "((-a) * b)" },
                new String[] { "!-a;", "(!(-a))" },
                new String[] { "a > b;", "(a > b)" },
                new String[] { "a < b;", "(a < b)" },
                new String[] { "a == b + 2;", "(a == (b + 2))" },
                new String[] { "5 > 2 == 2 > 1;", "((5 > 2) == (2 > 1))" },
                new String[] { "true == !false;", "(true == (!false))" },
        };
        testExpressions(tests);
    }

    @Test
    public void parseParenShouldReturnCorrectExpression() {
        var tests = new String[][] {
                new String[] { "(a + b) / 2;", "((a + b) / 2)" },
                new String[] { "1 + ((a + 1) * 2) / 3 + 3;", "((1 + (((a + 1) * 2) / 3)) + 3)" },
                new String[] { "a * (a +b);c + d", "(a * (a + b))(c + d)" },
        };
        testExpressions(tests);
    }

    @Test
    public void parseExpressionShouldWorkForBooleanValues() {
        String input = "let foo = true;let bar = false;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(2, statements.size());
        Bool firstBool = statements.get(0).as(Let.class).getExpression().as(Bool.class);
        assertEquals(true, firstBool.getValue());
        Bool secondBool = statements.get(1).as(Let.class).getExpression().as(Bool.class);
        assertEquals(false, secondBool.getValue());
    }

    @Test
    public void parseIfStatementShouldReturnAllIfComponents() {
        String input = """
                if (x > y) {
                    return y;
                } else {
                    return x;
                }
                """;
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        var ifStatement = statements.get(0).as(If.class);
        assertNotNull(ifStatement.getCondition());
        assertEquals(1, ifStatement.getIfBody().getStatements().size());
        assertNotNull(ifStatement.getIfBody().getStatements().get(0).as(Return.class));
        assertEquals(1, ifStatement.getElseBody().getStatements().size());
        assertNotNull(ifStatement.getElseBody().getStatements().get(0).as(Return.class));
    }

    @Test
    public void parseFunctionShouldReturnFunctionWithComponents() {
        String input = """
                function(abc, xyz) {
                    let x = 1;
                    return abc + xyz + x;
                }
                """;
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        var fn = statements.get(0).as(ExpressionStm.class).getExpression().as(Function.class);
        assertEquals(2, fn.getParams().size());
        assertEquals("abc", fn.getParams().get(0).getToken().getLiteral());
        assertEquals("xyz", fn.getParams().get(1).getToken().getLiteral());
        assertEquals(2, fn.getBody().getStatements().size());
    }

    private void testExpressions(String[][] tests) {
        for (String[] test : tests) {
            String input = test[0];
            String expected = test[1];
            List<Statement> statements = new Parser(input).parse();
            StringBuilder actual = new StringBuilder();
            for (Statement statement : statements) {
                var expression = statement.as(ExpressionStm.class).getExpression();
                actual.append(expression.toString());
            }
            assertEquals(expected, actual.toString());
        }
    }
}
