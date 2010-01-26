package com.stuffwithstuff.lark;

import java.util.*;

public class FunctionExpr extends Expr {
    public FunctionExpr(List<String> parameters, Expr body) {
        mParameters = new ArrayList<String>(parameters);
        mBody = body;
    }
    
    public List<String> getParameters() {
        return mParameters;
    }
    
    public Expr getBody() {
        return mBody;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    private final List<String> mParameters;
    private final Expr mBody;
}
