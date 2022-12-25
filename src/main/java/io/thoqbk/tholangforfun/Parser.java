package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.thoqbk.tholangforfun.ast.Expression;
import io.thoqbk.tholangforfun.ast.Int;
import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.ReturnStatement;
import io.thoqbk.tholangforfun.ast.Statement;

public class Parser {
    private final Lexer lexer;
    private Map<TokenType, Supplier<Expression>> prefixParsers = Map.of(
            TokenType.IDENT, this::parseIdentifier,
            TokenType.INT, this::parseInt);

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public List<Statement> parse() {
        List<Statement> retVal = new ArrayList<>();
        while (true) {
            Token token = lexer.nextToken();
            if (token.getType() == TokenType.EOF) {
                break;
            }
            switch (token.getType()) {
                case LET: {
                    retVal.add(parseLetStatement());
                    break;
                }
                case RETURN: {
                    retVal.add(parseReturnStatement());
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
        assertPeekToken(TokenType.ASSIGN);
        lexer.nextToken();
        lexer.nextToken();
        retVal.setExpression(parseExpression());
        return retVal;
    }

    private Statement parseReturnStatement() {
        ReturnStatement retVal = new ReturnStatement(lexer.currentToken());
        lexer.nextToken();
        retVal.setValue(parseExpression());
        return retVal;
    }

    private Expression parseExpression() {
        Token token = lexer.currentToken();
        Supplier<Expression> prefix = prefixParsers.get(token.getType());
        if (prefix == null) {
            throw new RuntimeException("Prefix parser not found for " + token.getType());
        }
        Expression retVal = prefix.get();
        assertPeekToken(TokenType.SEMICOLON);
        return retVal;
    }

    private Expression parseIdentifier() {
        return new Expression(lexer.currentToken());
    }

    private Expression parseInt() {
        return new Int(lexer.currentToken());
    }

    private Token assertPeekToken(TokenType type) {
        Token retVal = lexer.peekToken();
        assertTokenType(retVal, type);
        return retVal;
    }

    private void assertTokenType(Token token, TokenType tokenType) {
        if (token.getType() != tokenType) {
            throw new RuntimeException("Expect next token " + tokenType + ", was " + token.getType());
        }
    }
}
