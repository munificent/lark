package com.stuffwithstuff.lark;

public abstract class CallableExpr extends Expr {

    public abstract Expr call(Interpreter interpreter, Scope parentScope, Expr argExpr);
}
