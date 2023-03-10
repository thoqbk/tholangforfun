package io.thoqbk.tholangforfun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.function.Supplier;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Call;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Function;
import io.thoqbk.tholangforfun.ast.expressions.Identifier;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.expressions.Str;
import io.thoqbk.tholangforfun.ast.statements.Block;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.If;
import io.thoqbk.tholangforfun.ast.statements.Let;
import io.thoqbk.tholangforfun.ast.statements.Put;
import io.thoqbk.tholangforfun.ast.statements.Return;
import io.thoqbk.tholangforfun.ast.statements.Statement;
import io.thoqbk.tholangforfun.ast.statements.While;
import io.thoqbk.tholangforfun.exceptions.ParserException;

public class Parser {
    private final Lexer lexer;
    private Map<TokenType, Supplier<Expression>> prefixParsers = Map.of(
            TokenType.IDENT, this::parseIdentifier,
            TokenType.INT, this::parseInt,
            TokenType.STRING, this::parseString,
            TokenType.TRUE, this::parseBool,
            TokenType.FALSE, this::parseBool,
            TokenType.MINUS, this::parsePrefixExpression,
            TokenType.BANG, this::parsePrefixExpression,
            TokenType.LPAREN, this::parseLeftParen,
            TokenType.FUNCTION, this::parseFunction);
    private Map<TokenType, java.util.function.Function<Expression, Expression>> infixParsers = Map.ofEntries(
            entry(TokenType.PLUS, this::parseInfixExpression),
            entry(TokenType.MINUS, this::parseInfixExpression),
            entry(TokenType.SLASH, this::parseInfixExpression),
            entry(TokenType.ASTERISK, this::parseInfixExpression),
            entry(TokenType.GT, this::parseInfixExpression),
            entry(TokenType.LT, this::parseInfixExpression),
            entry(TokenType.GTE, this::parseInfixExpression),
            entry(TokenType.LTE, this::parseInfixExpression),
            entry(TokenType.EQ, this::parseInfixExpression),
            entry(TokenType.NOT_EQ, this::parseInfixExpression),
            entry(TokenType.LPAREN, this::parseFunctionCall),
            entry(TokenType.ASSIGN, this::parseInfixExpression));
    private static final int LOWEST_PRECEDENCE = 0;
    private Map<TokenType, Integer> precedences = Map.ofEntries(
            entry(TokenType.RPAREN, LOWEST_PRECEDENCE),
            entry(TokenType.EOF, LOWEST_PRECEDENCE),
            entry(TokenType.ASSIGN, 1),
            entry(TokenType.EQ, 2),
            entry(TokenType.NOT_EQ, 2),
            entry(TokenType.GT, 3),
            entry(TokenType.LT, 3),
            entry(TokenType.GTE, 3),
            entry(TokenType.LTE, 3),
            entry(TokenType.PLUS, 4),
            entry(TokenType.MINUS, 4),
            entry(TokenType.SLASH, 5),
            entry(TokenType.ASTERISK, 5),
            entry(TokenType.LPAREN, 6));
    private static final int PREFIX_PRECEDENCE = 7;

    public Parser(String input) {
        lexer = new Lexer(input);
    }

    public Program parse() {
        List<Statement> statements = new ArrayList<>();
        while (!peekTokenIs(TokenType.EOF)) {
            lexer.nextToken();
            statements.add(parseStatement());
        }
        Program retVal = new Program();
        retVal.setStatments(statements);
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
            case PUT: {
                return parsePutStatement();
            }
            case WHILE: {
                return parseWhileStatement();
            }
            default: {
                return parseExpressionStatement();
            }
        }
    }

    private Statement parseLetStatement() {
        Let retVal = new Let(lexer.currentToken());
        Token variable = assertPeekTokenThenNext(TokenType.IDENT);
        retVal.setVariableName(variable.getLiteral());
        assertPeekToken(TokenType.ASSIGN);
        lexer.nextToken();
        lexer.nextToken();
        retVal.setExpression(parseExpression());
        if (retVal.getExpression() != null && retVal.getExpression().is(Function.class)) {
            if (peekTokenIs(TokenType.SEMICOLON)) {
                lexer.nextToken();
            }
        } else {
            assertPeekTokenThenNext(TokenType.SEMICOLON);
        }
        return retVal;
    }

    private Statement parseReturnStatement() {
        Return retVal = new Return(lexer.currentToken());
        lexer.nextToken();
        if (currentTokenIs(TokenType.SEMICOLON)) {
            return retVal;
        }
        retVal.setValue(parseExpression());
        assertPeekTokenThenNext(TokenType.SEMICOLON);
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
            throw new ParserException("Prefix parser not found for token '" + token.getType() + "'");
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

    private Expression parseString() {
        return new Str(lexer.currentToken());
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

    private Statement parsePutStatement() {
        var retVal = new Put(lexer.currentToken());
        lexer.nextToken();
        retVal.setExpression(parseExpression());
        assertPeekTokenThenNext(TokenType.SEMICOLON);
        return retVal;
    }

    private Statement parseWhileStatement() {
        var retVal = new While(lexer.currentToken());
        assertPeekTokenThenNext(TokenType.LPAREN);
        lexer.nextToken();
        retVal.setCondition(parseExpression());
        assertPeekTokenThenNext(TokenType.RPAREN);
        assertPeekTokenThenNext(TokenType.LBRACE);
        retVal.setBody(parseBlockStatement());
        assertCurrentToken(TokenType.RBRACE);
        return retVal;
    }

    private Block parseBlockStatement() {
        Block retVal = new Block(lexer.currentToken());
        lexer.nextToken();
        while (!currentTokenIs(TokenType.RBRACE)) {
            retVal.getStatements().add(parseStatement());
            lexer.nextToken();
        }
        assertCurrentToken(TokenType.RBRACE);
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
        while (!currentTokenIs(TokenType.RPAREN)) {
            assertCurrentToken(TokenType.IDENT);
            retVal.add(parseExpression().as(Identifier.class));
            if (peekTokenIs(TokenType.COMMA)) {
                lexer.nextToken();
            }
            lexer.nextToken();
        }
        return retVal;
    }

    private Expression parseFunctionCall(Expression left) {
        assertCurrentToken(TokenType.LPAREN);
        Call retVal = null;
        if (left.is(Identifier.class) || left.is(Function.class)) {
            retVal = new Call(lexer.currentToken(), left);
        } else {
            throw new ParserException("Invalid left expression for funcation call " + left);
        }
        lexer.nextToken();
        retVal.setArgs(parseFunctionCallArgs());
        return retVal;
    }

    private List<Expression> parseFunctionCallArgs() {
        List<Expression> retVal = new ArrayList<>();
        while (!currentTokenIs(TokenType.RPAREN)) {
            retVal.add(parseExpression());
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

    private Token assertCurrentToken(TokenType type) {
        assertTokenType(lexer.currentToken(), type);
        return lexer.currentToken();
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

    private boolean currentTokenIs(TokenType type) {
        return lexer.currentToken().getType() == type;
    }

    private void assertTokenType(Token token, TokenType tokenType) {
        if (token.getType() != tokenType) {
            throw new ParserException(
                    String.format("Expect token to be '%s', received '%s' instead", tokenType, token.getType()));
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
            throw new ParserException("Precedence not found for token '" + type + "'");
        }
        return precedences.get(type);
    }
}
