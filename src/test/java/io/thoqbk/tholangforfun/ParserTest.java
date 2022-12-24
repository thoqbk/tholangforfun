package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.thoqbk.tholangforfun.ast.LetStatement;
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
}
