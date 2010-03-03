package com.stuffwithstuff.lark;


public final class Token {
    public Token(final TokenType type) {
        mType = type;
        mStringValue = "";
        mDoubleValue = 0;
    }

    public Token(final TokenType type, final String value) {
        mType = type;
        mStringValue = value;
        mDoubleValue = 0;
    }

    public Token(final TokenType type, final double value) {
        mType = type;
        mStringValue = "";
        mDoubleValue = value;
    }

    public TokenType getType() { return mType; }
    
    public String getString() { return mStringValue; }
    public double getDouble() { return mDoubleValue; }
    
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
            case LINE: return ";";
            case DOT: return ".";

            case NAME: return "[name] " + mStringValue;
            case OPERATOR: return "[op] " + mStringValue;
            case KEYWORD: return "[key] " + mStringValue;

            case NUMBER: return Double.toString(mDoubleValue);
            case STRING: return "\"" + mStringValue + "\"";

            case EOF: return "[eof]";

            default: return "[unknown token?!]";
        }
    }
    
    private final TokenType mType;
    private final String mStringValue;
    private final double mDoubleValue;
}
