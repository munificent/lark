package com.stuffwithstuff.lark;

public class CallExpr extends Expr {

    public CallExpr(final Expr left, final Expr right) {
        mLeft = left;
        mRight = right;
    }
    
    public Expr getLeft() { return mLeft; }
    public Expr getRight() { return mRight; }
    
    @Override
    public String toString() {
        return mLeft.toString() + " " + mRight.toString();
    }

    private final Expr mLeft;
    private final Expr mRight;
}
