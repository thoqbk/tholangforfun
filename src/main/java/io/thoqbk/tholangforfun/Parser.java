package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;

import io.thoqbk.tholangforfun.ast.Expression;
import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.Statement;

public class Parser {
    private final Lexer lexer;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public List<Statement> parse() {
        List<Statement> retVal = new ArrayList<>();
        while (true) {
            Token token = lexer.nextToken();
            if (token == null || token.getType() == TokenType.EOF) {
                break;
            }
            switch(token.getType()) {
                case LET: {
                    retVal.add(parseLetStatement());
                    break;
                }
                default: {
                    continue;
                }
            }
        }
        return retVal;
    }

    private Statement parseLetStatement() {
        LetStatement retVal = new LetStatement(lexer.currentToken());
        Token variable = lexer.nextToken();
        assertTokenType(variable, TokenType.IDENT);
        retVal.setVariableName(variable.getLiteral());
        assertPeekTokenAndNext(TokenType.ASSIGN);
        retVal.setExpression(parseExpression());
        return retVal;
    }

    private Expression parseExpression() {
        while (true) {
            Token token = lexer.nextToken();
            if (token == null || token.getType() == TokenType.EOF || token.getType() == TokenType.SEMICOLON) {
                break;
            }
        }
        return null;
    }

    private Token assertPeekTokenAndNext(TokenType type) {
        Token next = lexer.nextToken();
        if (next.getType() != type) {
            assertTokenType(next, type);
        }
        return lexer.nextToken();
    }

    private void assertTokenType(Token token, TokenType tokenType) {
        if (token.getType() != tokenType) {
            throw new RuntimeException("Expect next token " + tokenType + ", was " + token.getType());
        }
    }
}
