package io.thoqbk.tholangforfun;

public class Lexer {
    private final String input;
    private int nextChIdx = 0;
    private int ch = -1;
    private Token currentToken;

    public Lexer(String input) {
        this.input = input;
    }

    public Token nextToken() {
        Token retVal;
        skipWhitespaces();
        int ch = readChar();
        if (ch < 0) {
            this.currentToken = null;
            return currentToken;
        }
        switch (ch) {
            case '=': {
                if (peekChar() == '=') {
                    retVal = new Token(TokenType.EQ, "" + Character.toString(ch) + Character.toString(peekChar()));
                    readChar();
                } else {
                    retVal = new Token(TokenType.ASSIGN, currentChar());
                }
                break;
            }
            case '+': {
                retVal = new Token(TokenType.PLUS, ch);
                break;
            }
            case '-': {
                retVal = new Token(TokenType.MINUS, ch);
                break;
            }
            case '!': {
                if (peekChar() == '=') {
                    retVal = new Token(TokenType.NOT_EQ, "" + Character.toString(ch) + Character.toString(peekChar()));
                    readChar();
                } else {
                    retVal = new Token(TokenType.BANG, ch);
                }
                break;
            }
            case '/': {
                retVal = new Token(TokenType.SLASH, ch);
                break;
            }
            case '*': {
                retVal = new Token(TokenType.ASTERISK, ch);
                break;
            }
            case '<': {
                retVal = new Token(TokenType.LT, ch);
                break;
            }
            case '>': {
                retVal = new Token(TokenType.GT, ch);
                break;
            }
            case ';': {
                retVal = new Token(TokenType.SEMICOLON, ch);
                break;
            }
            case ',': {
                retVal = new Token(TokenType.COMMA, ch);
                break;
            }
            case '{': {
                retVal = new Token(TokenType.LBRACE, ch);
                break;
            }
            case '}': {
                retVal = new Token(TokenType.RBRACE, ch);
                break;
            }
            case '(': {
                retVal = new Token(TokenType.LPAREN, ch);
                break;
            }
            case ')': {
                retVal = new Token(TokenType.RPAREN, ch);
                break;
            }
            case 0: {
                retVal = new Token(TokenType.EOF, ch);
                break;
            }
            default: {
                if (isLetter(ch)) {
                    String literal = readIdentifier();
                    retVal = new Token(TokenType.lookupIdent(literal), literal);
                } else if (isDigit(ch)) {
                    retVal = new Token(TokenType.INT, readNumber());
                } else {
                    retVal = new Token(TokenType.ILLEGAL, ch);
                }
            }
        }
        currentToken = retVal;
        return retVal;
    }

    public Token currentToken() {
        return this.currentToken;
    }

    private int readChar() {
        if (nextChIdx >= input.length()) {
            return -1;
        }
        ch = input.charAt(nextChIdx++);
        return ch;
    }

    private int currentChar() {
        return ch;
    }

    private int peekChar() {
        if (nextChIdx >= input.length()) {
            return -1;
        }
        return input.charAt(nextChIdx);
    }

    private String readNumber() {
        int start = nextChIdx - 1;
        while (isDigit(peekChar())) {
            readChar();
        }
        return input.substring(start, nextChIdx);
    }

    private String readIdentifier() {
        int start = nextChIdx - 1;
        while (isLetter(peekChar())) {
            readChar();
        }
        return input.substring(start, nextChIdx);
    }

    private void skipWhitespaces() {
        while (isWhitespace(peekChar())) {
            readChar();
        }
    }

    private static boolean isLetter(int ch) {
        return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_';
    }

    private static boolean isDigit(int ch) {
        return '0' <= ch && ch <= '9';
    }

    private static boolean isWhitespace(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }
}
