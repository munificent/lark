package com.stuffwithstuff.lark;

import java.util.*;

public abstract class Expr {

    public static Expr unit() {
        // an empty list is unit
        return new ListExpr(new ArrayList<Expr>());
    }
    
    public abstract ExprType getType();
}
