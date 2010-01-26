package com.stuffwithstuff.lark;

public class IntExpr extends Expr {

    public IntExpr(final int value) {
        mValue = value;
    }
    
    public int getValue() { return mValue; }
    
    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public String toString() { return Integer.toString(mValue); }
 
    private final int mValue;
}
