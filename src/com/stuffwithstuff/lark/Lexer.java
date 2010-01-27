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
            
            // tack on a '\0' to the end of the string an lex it. that will let
            // us conveniently have a place to end any token that goes to the
            // end of the string
            char c = (mIndex < mText.length()) ? mText.charAt(mIndex) : '\0';

            switch (mState) {
            case DEFAULT:
                switch (c) {
                case '(': return singleCharToken(TokenType.LEFT_PAREN);
                case ')': return singleCharToken(TokenType.RIGHT_PAREN);
                case '[': return singleCharToken(TokenType.LEFT_BRACKET);
                case ']': return singleCharToken(TokenType.RIGHT_BRACKET);
                case '{': return singleCharToken(TokenType.LEFT_BRACE);
                case '}': return singleCharToken(TokenType.RIGHT_BRACE);
                case ',': return singleCharToken(TokenType.COMMA);
                case ';': return singleCharToken(TokenType.SEMICOLON);
                case '.': return singleCharToken(TokenType.DOT);
                
                case ':':
                    // start a multi-character token so that ":::" is a single
                    // keyword
                    startToken(LexState.IN_KEYWORD);
                    break;

                // ignore whitespace
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                case '\0':
                    mIndex++;
                    break;

                default:
                    if (isAlpha(c)) {
                        startToken(LexState.IN_NAME);
                    } else if (isOperator(c)) {
                        startToken(LexState.IN_OPERATOR);
                    } else if (isDigit(c)) {
                        startToken(LexState.IN_NUMBER);
                    } else {
                        //### bob: hack temp. unexpected character
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
                    mState = LexState.IN_KEYWORD;
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

            case IN_KEYWORD:
                if (isOperator(c) || isAlpha(c) || isDigit(c) || (c == ':')) {
                    mIndex++;
                } else {
                    return createStringToken(TokenType.KEYWORD);
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
    
    private Token singleCharToken(TokenType type) {
        mIndex++;
        return new Token(type, mText.substring(mIndex - 1, mIndex));
    }

    private void startToken(LexState state) {
        mTokenStart = mIndex;
        mState = state;
        mIndex++;        
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
               (c == '_') || (c == '\'');
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
        IN_KEYWORD,
        IN_NUMBER
    }

    private final String mText;
    private LexState mState;
    private int mTokenStart;
    private int mIndex;
}
