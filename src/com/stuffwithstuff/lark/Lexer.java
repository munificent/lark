package com.stuffwithstuff.lark;

public class Lexer {

    public Lexer(String text) {
        mText = text;
        mState = LexState.Default;
        mIndex = 0;
        mTokenStart = 0;
    }

    public Token readToken() {
        while (mIndex <= mText.length()) {
            char c = (mIndex < mText.length()) ? mText.charAt(mIndex) : '\0';

            switch (mState) {
            case Default:
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
                        mState = LexState.InName;
                        mIndex++;
                    } else if (isOperator(c)) {
                        mTokenStart = mIndex;
                        mState = LexState.InOperator;
                        mIndex++;
                    } else if (isDigit(c)) {
                        mTokenStart = mIndex;
                        mState = LexState.InNumber;
                        mIndex++;
                    } else {
                        //### bob: hack temp
                        return new Token(TokenType.EOF);
                    }
                    break;
                }
                break;

            case InName:
                if (isAlpha(c) || isDigit(c) || isOperator(c)) {
                    mIndex++;
                } else if (c == ':') {
                    mIndex++;
                    String name = mText.substring(mTokenStart, mIndex);
                    mState = LexState.Default;
                    return new Token(TokenType.KEYWORD, name);
                } else {
                    String name = mText.substring(mTokenStart, mIndex);
                    mState = LexState.Default;
                    return new Token(TokenType.NAME, name);
                }
                break;

            case InOperator:
                if (isOperator(c) || isAlpha(c) || isDigit(c)) {
                    mIndex++;
                } else {
                    String name = mText.substring(mTokenStart, mIndex);
                    mState = LexState.Default;
                    return new Token(TokenType.OPERATOR, name);
                }
                break;

            case InNumber:
                if (isDigit(c)) {
                    mIndex++;
                } else {
                    String name = mText.substring(mTokenStart, mIndex);
                    int value = Integer.parseInt(name);
                    mState = LexState.Default;
                    return new Token(TokenType.NUMBER, value);
                }
                break;
            }
        }
        
        return new Token(TokenType.EOF);
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
        Default,
        InName,
        InOperator,
        InNumber
    }

    private final String mText;
    private LexState mState;
    private int mTokenStart;
    private int mIndex;
}
