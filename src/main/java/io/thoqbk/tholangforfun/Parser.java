package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.thoqbk.tholangforfun.ast.ExpressionStatement;
import io.thoqbk.tholangforfun.ast.LetStatement;
import io.thoqbk.tholangforfun.ast.ReturnStatement;
import io.thoqbk.tholangforfun.ast.Statement;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
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
            TokenType.TRUE, this::parseBool,
            TokenType.FALSE, this::parseBool,
            TokenType.MINUS, this::parsePrefixExpression,
            TokenType.BANG, this::parsePrefixExpression,
            TokenType.LPAREN, this::parseLeftParen);
    private Map<TokenType, Function<Expression, Expression>> infixParsers = Map.of(
            TokenType.PLUS, this::parseInfixExpression,
            TokenType.MINUS, this::parseInfixExpression,
            TokenType.SLASH, this::parseInfixExpression,
            TokenType.ASTERISK, this::parseInfixExpression,
            TokenType.GT, this::parseInfixExpression,
            TokenType.LT, this::parseInfixExpression,
            TokenType.EQ, this::parseInfixExpression,
            TokenType.NOT_EQ, this::parseInfixExpression);
    private static final int LOWEST_PRECEDENCE = 0;
    private Map<TokenType, Integer> precedences = Map.of(
            TokenType.RPAREN, LOWEST_PRECEDENCE,
            TokenType.EQ, 1,
            TokenType.NOT_EQ, 1,
            TokenType.GT, 2,
            TokenType.LT, 2,
            TokenType.PLUS, 3,
            TokenType.MINUS, 3,
            TokenType.SLASH, 4,
            TokenType.ASTERISK, 4);
    private static final int PREFIX_PRECEDENCE = 5;

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
                    retVal.add(parseExpressionStatement());
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
        assertTokenType(lexer.nextToken(), TokenType.SEMICOLON);
        return retVal;
    }

    private Statement parseReturnStatement() {
        ReturnStatement retVal = new ReturnStatement(lexer.currentToken());
        lexer.nextToken();
        retVal.setValue(parseExpression());
        assertTokenType(lexer.nextToken(), TokenType.SEMICOLON);
        return retVal;
    }

    private Statement parseExpressionStatement() {
        var retVal = new ExpressionStatement(parseExpression());
        if (peekTokenIs(TokenType.SEMICOLON)) {
            lexer.nextToken();
        }
        return retVal;
    }

    private Expression parseExpression() {
        return parseExpression(LOWEST_PRECEDENCE);
    }

    private Expression parseExpression(int prevPrecedence) {
        Token token = lexer.currentToken();
        Supplier<Expression> prefix = prefixParsers.get(token.getType());
        if (prefix == null) {
            throw new RuntimeException("Prefix parser not found for token '" + token.getType() + "'");
        }
        Expression left = prefix.get();
        while (true) {
            if (peekTokenIs(TokenType.SEMICOLON)) {
                return left;
            }
            if (peekPrecedence() <= prevPrecedence) {
                break;
            }
            lexer.nextToken();
            var infix = infixParsers.get(lexer.currentToken().getType());
            if (infix == null) {
                throw new RuntimeException("Infix parser not found for token '" + lexer.currentToken().getType() + "'");
            }
            left = infix.apply(left);
        }
        return left;
    }

    private Expression parsePrefixExpression() {
        Prefix retVal = new Prefix(lexer.currentToken());
        lexer.nextToken();
        retVal.setRight(parseExpression(PREFIX_PRECEDENCE));
        return retVal;
    }

    private Expression parseInfixExpression(Expression left) {
        Infix retVal = new Infix(lexer.currentToken());
        int precedence = currentPrecedence();
        retVal.setLeft(left);
        lexer.nextToken();
        retVal.setRight(parseExpression(precedence));
        return retVal;
    }

    private Expression parseIdentifier() {
        return new Identifier(lexer.currentToken());
    }

    private Expression parseInt() {
        return new Int(lexer.currentToken());
    }

    private Expression parseBool() {
        return new Bool(lexer.currentToken());
    }

    private Expression parseLeftParen() {
        lexer.nextToken();
        var retVal = parseExpression();
        assertPeekToken(TokenType.RPAREN);
        lexer.nextToken();
        return retVal;
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

    private int currentPrecedence() {
        TokenType type = lexer.currentToken().getType();
        return precedence(type);
    }

    private int peekPrecedence() {
        TokenType type = lexer.peekToken().getType();
        return precedence(type);
    }

    private int precedence(TokenType type) {
        if (!precedences.containsKey(type)) {
            throw new RuntimeException("Precedence not found for token '" + type + "'");
        }
        return precedences.get(type);
    }
}
