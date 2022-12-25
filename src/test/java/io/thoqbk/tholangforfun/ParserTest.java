package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.Int;
import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.PrefixExpression;
import io.thoqbk.tholangforfun.ast.ReturnStatement;
import io.thoqbk.tholangforfun.ast.Statement;

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
        PrefixExpression prefix = statements.get(0).as(LetStatement.class).getExpression().as(PrefixExpression.class);
        assertEquals(TokenType.MINUS, prefix.getToken().getType());
        Int intExpression = prefix.getRight().as(Int.class);
        assertEquals(1000, intExpression.getValue());
    }
}
