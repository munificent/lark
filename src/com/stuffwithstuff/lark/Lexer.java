package com.stuffwithstuff.lark;

public class Lexer {

    public Lexer(String text) {
        mText = text;
        mState = LexState.DEFAULT;
        mIndex = 0;
        mTokenStart = 0;
    }

    public Token readToken() {
        while (mIndex <= mText.length()) {
            char c = (mIndex < mText.length()) ? mText.charAt(mIndex) : '\0';

            switch (mState) {
            case DEFAULT:
                switch (c) {
                case '(':
                    mIndex++;
                    return new Token(TokenType.LEFT_PAREN);

                case ')':
                    mIndex++;
                    return new Token(TokenType.RIGHT_PAREN);

                case '[':
                    mIndex++;
                    return new Token(TokenType.LEFT_BRACKET);

                case ']':
                    mIndex++;
                    return new Token(TokenType.RIGHT_BRACKET);

                case '{':
                    mIndex++;
                    return new Token(TokenType.LEFT_BRACE);

                case '}':
                    mIndex++;
                    return new Token(TokenType.RIGHT_BRACE);

                case ',':
                    mIndex++;
                    return new Token(TokenType.COMMA);

                case ':':
                    mIndex++;
                    return new Token(TokenType.KEYWORD, ":");

                case ' ':
                case '\n':
                case '\r':
                case '\0':
                    mIndex++;
                    break;

                default:
                    if (isAlpha(c)) {
                        mTokenStart = mIndex;
                        mState = LexState.IN_NAME;
                        mIndex++;
                    } else if (isOperator(c)) {
                        mTokenStart = mIndex;
                        mState = LexState.IN_OPERATOR;
                        mIndex++;
                    } else if (isDigit(c)) {
                        mTokenStart = mIndex;
                        mState = LexState.IN_NUMBER;
                        mIndex++;
                    } else {
                        //### bob: hack temp
                        return new Token(TokenType.EOF);
                    }
                    break;
                }
                break;

            case IN_NAME:
                if (isAlpha(c) || isDigit(c) || isOperator(c)) {
                    mIndex++;
                } else if (c == ':') {
                    mIndex++;
                    return createStringToken(TokenType.KEYWORD);
                } else {
                    return createStringToken(TokenType.NAME);
                }
                break;

            case IN_OPERATOR:
                if (isOperator(c) || isAlpha(c) || isDigit(c)) {
                    mIndex++;
                } else {
                    return createStringToken(TokenType.OPERATOR);
                }
                break;

            case IN_NUMBER:
                if (isDigit(c)) {
                    mIndex++;
                } else {
                    return createIntToken(TokenType.NUMBER);
                }
                break;
            }
        }
        
        return new Token(TokenType.EOF);
    }

    private Token createStringToken(TokenType type) {
        String text = mText.substring(mTokenStart, mIndex);
        mState = LexState.DEFAULT;
        return new Token(type, text);
    }

    private Token createIntToken(TokenType type) {
        String text = mText.substring(mTokenStart, mIndex);
        int value = Integer.parseInt(text);
        mState = LexState.DEFAULT;
        return new Token(type, value);
    }
    
    private boolean isAlpha(final char c) {
        return ((c >= 'a') && (c <= 'z')) || 
               ((c >= 'A') && (c <= 'Z')) ||
               (c == '_');
    }

    private boolean isDigit(final char c) {
        return (c >= '0') && (c <= '9');
    }

    private boolean isOperator(final char c) {
        // note: ` and . are not operators
        return "~!@#$%^&*-=+\\|/?<>".indexOf(c) != -1;
    }

    private enum LexState {
        DEFAULT,
        IN_NAME,
        IN_OPERATOR,
        IN_NUMBER
    }

    private final String mText;
    private LexState mState;
    private int mTokenStart;
    private int mIndex;
}
