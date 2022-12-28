package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Function;
import io.thoqbk.tholangforfun.ast.expressions.Identifier;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.statements.Block;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.If;
import io.thoqbk.tholangforfun.ast.statements.Let;
import io.thoqbk.tholangforfun.ast.statements.Return;
import io.thoqbk.tholangforfun.ast.statements.Statement;

public class Parser {
    private final Lexer lexer;
    private Map<TokenType, Supplier<Expression>> prefixParsers = Map.of(
            TokenType.IDENT, this::parseIdentifier,
            TokenType.INT, this::parseInt,
            TokenType.TRUE, this::parseBool,
            TokenType.FALSE, this::parseBool,
            TokenType.MINUS, this::parsePrefixExpression,
            TokenType.BANG, this::parsePrefixExpression,
            TokenType.LPAREN, this::parseLeftParen,
            TokenType.FUNCTION, this::parseFunction);
    private Map<TokenType, java.util.function.Function<Expression, Expression>> infixParsers = Map.of(
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
            TokenType.EOF, LOWEST_PRECEDENCE,
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
        while (lexer.peekToken().getType() != TokenType.EOF) {
            lexer.nextToken();
            retVal.add(parseStatement());
        }
        return retVal;
    }

    private Statement parseStatement() {
        Token token = lexer.currentToken();
        switch (token.getType()) {
            case LET: {
                return parseLetStatement();
            }
            case RETURN: {
                return parseReturnStatement();
            }
            case IF: {
                return parseIfStatement();
            }
            default: {
                return parseExpressionStatement();
            }
        }
    }

    private Statement parseLetStatement() {
        Let retVal = new Let(lexer.currentToken());
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
        Return retVal = new Return(lexer.currentToken());
        lexer.nextToken();
        retVal.setValue(parseExpression());
        assertTokenType(lexer.nextToken(), TokenType.SEMICOLON);
        return retVal;
    }

    private Statement parseExpressionStatement() {
        var retVal = new ExpressionStm(parseExpression());
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
            var infix = infixParsers.get(lexer.peekToken().getType());
            if (infix == null || peekTokenIs(TokenType.SEMICOLON) || peekPrecedence() <= prevPrecedence) {
                break;
            }
            lexer.nextToken();
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

    private Statement parseIfStatement() {
        var retVal = new If(lexer.currentToken());
        assertPeekTokenThenNext(TokenType.LPAREN);
        lexer.nextToken();
        retVal.setCondition(parseExpression());
        assertPeekTokenThenNext(TokenType.RPAREN);
        assertPeekTokenThenNext(TokenType.LBRACE);
        retVal.setIfBody(parseBlockStatement());
        if (peekTokenIs(TokenType.ELSE)) {
            lexer.nextToken();
            lexer.nextToken();
            retVal.setElseBody(parseBlockStatement());
        }
        return retVal;
    }

    private Block parseBlockStatement() {
        Block retVal = new Block(lexer.currentToken());
        lexer.nextToken();
        while (lexer.currentToken().getType() != TokenType.RBRACE) {
            retVal.getStatements().add(parseStatement());
            lexer.nextToken();
        }
        assertTokenType(lexer.currentToken(), TokenType.RBRACE);
        return retVal;
    }

    private Expression parseFunction() {
        Function retVal = new Function(lexer.currentToken());
        assertPeekTokenThenNext(TokenType.LPAREN);
        lexer.nextToken();
        retVal.setParams(parseFunctionParams());
        assertPeekTokenThenNext(TokenType.LBRACE);
        retVal.setBody(parseBlockStatement());
        return retVal;
    }

    private List<Identifier> parseFunctionParams() {
        List<Identifier> retVal = new ArrayList<>();
        while (lexer.currentToken().getType() != TokenType.RPAREN) {
            assertTokenType(lexer.currentToken(), TokenType.IDENT);
            retVal.add(parseExpression().as(Identifier.class));
            if (peekTokenIs(TokenType.COMMA)) {
                lexer.nextToken();
            }
            lexer.nextToken();
        }
        return retVal;
    }

    private Token assertPeekToken(TokenType type) {
        Token retVal = lexer.peekToken();
        assertTokenType(retVal, type);
        return retVal;
    }

    private Token assertPeekTokenThenNext(TokenType type) {
        Token retVal = lexer.peekToken();
        assertTokenType(retVal, type);
        lexer.nextToken();
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
