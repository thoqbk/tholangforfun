package io.thoqbk.tholangforfun;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LexerTest {
    @Test
    public void simpleAssignmentShouldReturnCorrectTokens() {
        String input = "let xyz = 10;";
        Token[] expected = new Token[]{
            new Token(TokenType.LET, "let"),
            new Token(TokenType.IDENT, "xyz"),
            new Token(TokenType.ASSIGN, "="),
            new Token(TokenType.INT, "10"),
            new Token(TokenType.SEMICOLON, ";")
        };
        testTokens(input, expected);
    }

    @Test
    public void shouldReturnCorrectTokens() {
        String input = """
            let five = 5;
            let ten = 10;
            
            let add = fn(x, y) {
              x + y;
            };
            
            let result = add(five, ten);
            !-/*5;
            5 < 10 > 5;
            
            if (5 < 10) {
                return true;
            } else {
                return false;
            }
            
            10 == 10;
            10 != 9;
                """;
        Token[] expected = new Token[]{
            new Token(TokenType.LET, "let"),
            new Token(TokenType.IDENT, "five"),
            new Token(TokenType.ASSIGN, "="),
            new Token(TokenType.INT, "5"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.LET, "let"),
            new Token(TokenType.IDENT, "ten"),
            new Token(TokenType.ASSIGN, "="),
            new Token(TokenType.INT, "10"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.LET, "let"),
            new Token(TokenType.IDENT, "add"),
            new Token(TokenType.ASSIGN, "="),
            new Token(TokenType.FUNCTION, "fn"),
            new Token(TokenType.LPAREN, "("),
            new Token(TokenType.IDENT, "x"),
            new Token(TokenType.COMMA, ","),
            new Token(TokenType.IDENT, "y"),
            new Token(TokenType.RPAREN, ")"),
            new Token(TokenType.LBRACE, "{"),
            new Token(TokenType.IDENT, "x"),
            new Token(TokenType.PLUS, "+"),
            new Token(TokenType.IDENT, "y"),
            new Token(TokenType.SEMICOLON, ";"),
            new Token(TokenType.RBRACE, "}"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.LET, "let"),
            new Token(TokenType.IDENT, "result"),
            new Token(TokenType.ASSIGN, "="),
            new Token(TokenType.IDENT, "add"),
            new Token(TokenType.LPAREN, "("),
            new Token(TokenType.IDENT, "five"),
            new Token(TokenType.COMMA, ","),
            new Token(TokenType.IDENT, "ten"),
            new Token(TokenType.RPAREN, ")"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.BANG, "!"),
            new Token(TokenType.MINUS, "-"),
            new Token(TokenType.SLASH, "/"),
            new Token(TokenType.ASTERISK, "*"),
            new Token(TokenType.INT, "5"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.INT, "5"),
            new Token(TokenType.LT, "<"),
            new Token(TokenType.INT, "10"),
            new Token(TokenType.GT, ">"),
            new Token(TokenType.INT, "5"),
            new Token(TokenType.SEMICOLON, ";"),

            new Token(TokenType.IF, "if"),
            new Token(TokenType.LPAREN, "("),
            new Token(TokenType.INT, "5"),
            new Token(TokenType.LT, "<"),
            new Token(TokenType.INT, "10"),
            new Token(TokenType.RPAREN, ")"),
            new Token(TokenType.LBRACE, "{"),
            new Token(TokenType.RETURN, "return"),
            new Token(TokenType.TRUE, "true"),
            new Token(TokenType.SEMICOLON, ";"),
            new Token(TokenType.RBRACE, "}"),
            new Token(TokenType.ELSE, "else"),
            new Token(TokenType.LBRACE, "{"),
            new Token(TokenType.RETURN, "return"),
            new Token(TokenType.FALSE, "false"),
            new Token(TokenType.SEMICOLON, ";"),
            new Token(TokenType.RBRACE, "}"),

            new Token(TokenType.INT, "10"),
            new Token(TokenType.EQ, "=="),
            new Token(TokenType.INT, "10"),
            new Token(TokenType.SEMICOLON, ";"),
            
            new Token(TokenType.INT, "10"),
            new Token(TokenType.NOT_EQ, "!="),
            new Token(TokenType.INT, "9"),
            new Token(TokenType.SEMICOLON, ";"),
        };
        testTokens(input, expected);
    }

    private void testTokens(String input, Token[] expected) {
        Lexer lexer = new Lexer(input);
        Token token = null;
        int expectedIdx = 0;
        while ((token = lexer.nextToken()) != null) {
            Token expectedToken = expected[expectedIdx++];
            assertEquals("Token types are not the same " + expectedIdx, expectedToken.getType(), token.getType());
            assertEquals("Token literals are not the same" + expectedIdx, expectedToken.getLiteral(), token.getLiteral());
        }
    }
}
