package com.stuffwithstuff.lark;

import java.text.NumberFormat;

public class NumExpr extends CallableExpr {
    public NumExpr(final double value) {
        mValue = value;
    }
    
    public double getValue() { return mValue; }
    
    @Override
    public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
        // an int is a "function" that takes a list and returns the element
        // at that (zero-based) index in the list
        // > 1 (4, 5, 6)
        // = 5
        // > (1, 2, 3).2
        // = 3
        Expr arg = interpreter.eval(scope, argExpr);

        if (!(arg instanceof ListExpr)) return interpreter.error("Argument to index function must be a list.");
        
        ListExpr list = (ListExpr)arg;
        
        int index = (int)mValue;
        
        if (index < 0) return interpreter.error("Index must be non-negative.");
        if (index >= list.getList().size()) return interpreter.error("Index is out of bounds.");
        
        return list.getList().get(index);
        
        //### bob: should also work for getting a character from a string?
    }
    
    @Override
    public ExprType getType() { return ExprType.NUMBER; }
    
    @Override
    public String toString() {
        return sFormat.format(mValue);
    }

    private static final NumberFormat sFormat;
    
    static {
        sFormat = NumberFormat.getInstance();
        sFormat.setGroupingUsed(false);
        sFormat.setMinimumFractionDigits(0);
    }
    
    private final double mValue;
}
