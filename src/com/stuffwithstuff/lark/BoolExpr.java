package com.stuffwithstuff.lark;

public class BoolExpr extends Expr {
    public BoolExpr(final boolean value) {
        mValue = value;
    }
    
    public boolean getValue() { return mValue; }
    
    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public String toString() { return Boolean.toString(mValue); }
 
    private final boolean mValue;
}
