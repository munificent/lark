package com.stuffwithstuff.lark;

public class StringExpr extends Expr {
    public StringExpr(final String value) {
        mValue = value;
    }
    
    public String getValue() { return mValue; }
    
    @Override
    public boolean isLiteral() {
        return true;
    }
    
    @Override
    public String toString() { return mValue; }
 
    private final String mValue;

}
