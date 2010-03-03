package com.stuffwithstuff.lark;

import java.util.*;

public class FunctionExpr extends CallableExpr {
    public FunctionExpr(Scope closure, boolean isMacro, List<String> parameters, Expr body) {
        mClosure = closure;
        mIsMacro = isMacro;
        mParameters = new ArrayList<String>(parameters);
        mBody = body;
    }
    
    @Override
    public Expr call(Interpreter interpreter, Scope parentScope, Expr argExpr) {
        // eagerly evaluate the arguments
        Expr arg = argExpr;
        if (!mIsMacro) {
            arg = interpreter.eval(parentScope, argExpr);
        }
        
        // make sure we have the right number of arguments
        if (mParameters.size() == 0) {
            if (!(arg instanceof ListExpr)) return interpreter.error("Function expects no arguments but got one.");
            if (((ListExpr)arg).getList().size() != 0) return interpreter.error("Function expects no arguments but got multiple.");
        } else if (mParameters.size() > 1) {
            if (!(arg instanceof ListExpr)) return interpreter.error("Function expects multiple arguments but got one.");
            if (((ListExpr)arg).getList().size() != mParameters.size()) return interpreter.error("Function did not get expected number of arguments.");
        }
        
        // create a new local scope for the function
        Scope scope = mClosure.create();
        
        // bind the arguments to the parameters
        if (mParameters.size() == 1) {
            scope.put(mParameters.get(0), arg);
        } else if (mParameters.size() > 1) {
            ListExpr args = (ListExpr)arg;
            for (int i = 0; i < mParameters.size(); i++) {
                scope.put(mParameters.get(i), args.getList().get(i));
            }
        }
        
        // evaluate the body in the new scope
        return interpreter.eval(scope, mBody);
    }
    
    private final Scope mClosure;
    private final boolean mIsMacro;
    private final List<String> mParameters;
    private final Expr mBody;
}
