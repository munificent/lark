package com.stuffwithstuff.lark;

public class NameExpr extends Expr {

    public NameExpr(final String name) {
        mName = name;
    }
    
    public String getName() { return mName; }
    
    @Override
    public ExprType getType() { return ExprType.NAME; }

    @Override
    public String toString() { return mName; }
    
    private final String mName;
}
