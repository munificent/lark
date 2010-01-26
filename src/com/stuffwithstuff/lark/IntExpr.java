package com.stuffwithstuff.lark;

public class IntExpr extends CallableExpr {
    public IntExpr(final int value) {
        mValue = value;
    }
    
    public int getValue() { return mValue; }
    
    @Override
    public boolean isLiteral() {
        return true;
    }
    
    @Override
    public Expr call(Interpreter interpreter, Environment scope, Expr argExpr) {
        // an int is a "function" that takes a list and returns the element
        // at that (zero-based) index in the list
        // > 1 (4, 5, 6)
        // = 5
        // > (1, 2, 3).2
        // = 3
        Expr arg = interpreter.eval(scope, argExpr);

        if (!(arg instanceof ListExpr)) return interpreter.error("Argument to index function must be a list.");
        
        ListExpr list = (ListExpr)arg;
        
        if (mValue < 0) return interpreter.error("Index must be non-negative.");
        if (mValue >= list.getList().size()) return interpreter.error("Index is out of bounds.");
        
        return list.getList().get(mValue);
    }
    
    @Override
    public String toString() { return Integer.toString(mValue); }
 
    private final int mValue;
}
