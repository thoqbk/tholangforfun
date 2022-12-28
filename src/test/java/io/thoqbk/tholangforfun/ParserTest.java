package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.ExpressionStatement;
import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.ReturnStatement;
import io.thoqbk.tholangforfun.ast.Statement;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;

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

        LetStatement let1 = statements.get(0).as(LetStatement.class);
        assertEquals(let1.getVariableName(), "x");

        LetStatement let2 = statements.get(1).as(LetStatement.class);
        assertEquals(let2.getVariableName(), "abc");
    }

    @Test
    public void parseReturnStatementShouldReturnCorrectToken() {
        String input = "return 10;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(statements.size(), 1);
        assertEquals(statements.get(0).as(ReturnStatement.class).getToken().getType(), TokenType.RETURN);
    }

    @Test
    public void parseExpressionShouldReturnCorrectIdentifier() {
        String input = "return foobar;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(statements.size(), 1);
        ReturnStatement stm = statements.get(0).as(ReturnStatement.class);
        assertEquals(TokenType.IDENT, stm.getValue().getToken().getType());
        assertEquals("foobar", stm.getValue().getToken().getLiteral());
    }

    @Test
    public void parsePrefixMinusShouldReturnPrefixExpression() {
        String input = "let abc = -1000;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        Prefix prefix = statements.get(0).as(LetStatement.class).getExpression().as(Prefix.class);
        assertEquals(TokenType.MINUS, prefix.getToken().getType());
        Int intExpression = prefix.getRight().as(Int.class);
        assertEquals(1000, intExpression.getValue());
    }

    @Test
    public void parseInfixShouldReturnLeftAndRight() {
        String input = "return 100 + 200;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(1, statements.size());
        Infix infix = statements.get(0).as(ReturnStatement.class).getValue().as(Infix.class);
        assertEquals(TokenType.PLUS, infix.getToken().getType());

        Int left = infix.getLeft().as(Int.class);
        Int right = infix.getRight().as(Int.class);
        assertEquals(100, left.getValue());
        assertEquals(200, right.getValue());
    }

    @Test
    public void parseExpressionShouldConsiderPrecedences() {
        String[][] tests = new String[][] {
                new String[] { "a + b;", "(a + b)" },
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
                new String[] { "true == !false;", "(true == (!false))" }
        };
        testExpressions(tests);
    }

    @Test
    public void parseParenShouldReturnCorrectExpression() {
        var tests = new String[][] {
                new String[] { "(a + b) / 2;", "((a + b) / 2)" },
                new String[] { "1 + ((a + 1) * 2) / 3 + 3;", "((1 + (((a + 1) * 2) / 3)) + 3)" },
        };
        testExpressions(tests);
    }

    @Test
    public void parseExpressionShouldWorkForBooleanValues() {
        String input = "let foo = true;let bar = false;";
        List<Statement> statements = new Parser(input).parse();
        assertEquals(2, statements.size());
        Bool firstBool = statements.get(0).as(LetStatement.class).getExpression().as(Bool.class);
        assertEquals(true, firstBool.getValue());
        Bool secondBool = statements.get(1).as(LetStatement.class).getExpression().as(Bool.class);
        assertEquals(false, secondBool.getValue());
    }

    private void testExpressions(String[][] tests) {
        for (String[] test : tests) {
            String input = test[0];
            String expected = test[1];
            List<Statement> statements = new Parser(input).parse();
            assertEquals(1, statements.size());
            var expression = statements.get(0).as(ExpressionStatement.class).getExpression();
            assertEquals(expected, expression.toString());
        }
    }
}
