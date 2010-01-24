package com.stuffwithstuff.lark;

public final class Token {
    public Token(final TokenType type) {
        mType = type;
        mStringValue = "";
        mIntValue = 0;
    }

    public Token(final TokenType type, final String value) {
        mType = type;
        mStringValue = value;
        mIntValue = 0;
    }

    public Token(final TokenType type, final int value) {
        mType = type;
        mStringValue = "";
        mIntValue = value;
    }

    public TokenType getType() { return mType; }
    
    public String getString() { return mStringValue; }
    public int    getInt()    { return mIntValue; }
    
    public String toString() {
        switch (mType)
        {
            case LEFT_PAREN: return "(";
            case RIGHT_PAREN: return ")";
            case LEFT_BRACKET: return "[";
            case RIGHT_BRACKET: return "]";
            case LEFT_BRACE: return "{";
            case RIGHT_BRACE: return "}";
            case COMMA: return ",";

            case NAME: return "[name] " + mStringValue;
            case OPERATOR: return "[op] " + mStringValue;
            case KEYWORD: return "[key] " + mStringValue;

            case NUMBER: return Integer.toString(mIntValue);

            case EOF: return "[eof]";

            default: return "[unknown token?!]";
        }
    }
    
    private final TokenType mType;
    private final String mStringValue;
    private final int mIntValue;
}
