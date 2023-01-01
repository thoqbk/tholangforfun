package io.thoqbk.tholangforfun;

public class Lexer {
    private final String input;
    private State state = new State();

    public Lexer(String input) {
        this.input = input;
    }

    public Token nextToken() {
        Token retVal;
        skipWhitespaces();
        int ch = readChar();
        if (ch < 0) {
            state.currentToken = new Token(TokenType.EOF, null);
            return state.currentToken;
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
            case '"': {
                retVal = new Token(TokenType.STRING, readString());
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
        state.currentToken = retVal;
        return retVal;
    }

    public Token currentToken() {
        return state.currentToken;
    }

    public Token peekToken() {
        State old = state.clone();
        Token retVal = nextToken();
        state = old;
        return retVal;
    }

    private int readChar() {
        if (state.nextChIdx >= input.length()) {
            return -1;
        }
        state.ch = input.charAt(state.nextChIdx++);
        return state.ch;
    }

    private int currentChar() {
        return state.ch;
    }

    private int peekChar() {
        if (state.nextChIdx >= input.length()) {
            return -1;
        }
        return input.charAt(state.nextChIdx);
    }

    private String readNumber() {
        int start = state.nextChIdx - 1;
        while (isDigit(peekChar())) {
            readChar();
        }
        return input.substring(start, state.nextChIdx);
    }

    private String readIdentifier() {
        int start = state.nextChIdx - 1;
        while (isLetter(peekChar())) {
            readChar();
        }
        return input.substring(start, state.nextChIdx);
    }

    private String readString() {
        int start = state.nextChIdx;
        while (peekChar() != '"') {
            readChar();
        }
        readChar();
        return input.substring(start, state.nextChIdx - 1);
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

    private static class State {
        private int nextChIdx = 0;
        private int ch = -1;
        private Token currentToken;

        public State clone() {
            State retVal = new State();
            retVal.nextChIdx = nextChIdx;
            retVal.ch = ch;
            retVal.currentToken = currentToken;
            return retVal;
        }
    }
}
