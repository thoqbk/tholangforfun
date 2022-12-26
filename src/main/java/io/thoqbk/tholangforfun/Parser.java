package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.ReturnStatement;
import io.thoqbk.tholangforfun.ast.Statement;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Identifier;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;

public class Parser {
    private final Lexer lexer;
    private Map<TokenType, Supplier<Expression>> prefixParsers = Map.of(
            TokenType.IDENT, this::parseIdentifier,
            TokenType.INT, this::parseInt,
            TokenType.MINUS, this::parsePrefixExpression);
    private Map<TokenType, Function<Expression, Expression>> infixParsers = Map.of(
            TokenType.PLUS, this::parseInfixExpression);

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
            throw new RuntimeException("Prefix parser not found for token '" + token.getType() + "'");
        }
        Expression left = prefix.get();
        if (peekTokenIs(TokenType.SEMICOLON)) {
            return left;
        }
        lexer.nextToken();
        var infix = infixParsers.get(lexer.currentToken().getType());
        if (infix == null) {
            throw new RuntimeException("Infix parser not found for token '" + lexer.currentToken().getType() + "'");
        }
        return parseInfixExpression(left);
    }

    private Expression parsePrefixExpression() {
        Prefix retVal = new Prefix(lexer.currentToken());
        lexer.nextToken();
        retVal.setRight(parseExpression());
        return retVal;
    }

    private Expression parseInfixExpression(Expression left) {
        Infix retVal = new Infix(lexer.currentToken());
        retVal.setLeft(left);
        lexer.nextToken();
        retVal.setRight(parseExpression());
        return retVal;
    }

    private Expression parseIdentifier() {
        return new Identifier(lexer.currentToken());
    }

    private Expression parseInt() {
        return new Int(lexer.currentToken());
    }

    private Token assertPeekToken(TokenType type) {
        Token retVal = lexer.peekToken();
        assertTokenType(retVal, type);
        return retVal;
    }

    private boolean peekTokenIs(TokenType type) {
        return lexer.peekToken().getType() == type;
    }

    private void assertTokenType(Token token, TokenType tokenType) {
        if (token.getType() != tokenType) {
            throw new RuntimeException(
                    "Expect token to be \'" + tokenType + "\', received \'" + token.getType() + "\' instead");
        }
    }
}
