package com.stuffwithstuff.lark;

import java.util.*;

public class ListExpr extends Expr {

    public ListExpr(final List<Expr> list) {
        mList = new ArrayList<Expr>(list);
    }
    
    public List<Expr> getList() { return mList; }
       
    @Override
    public boolean isLiteral() {
        // an empty list is () ("unit"), which evaluates to itself
        return mList.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("(");
        
        Iterator<?> iter = mList.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            
            if (iter.hasNext()) {
                builder.append(", ");
            }
        }
        
        builder.append(")");
        
        return builder.toString();
    }
    
    private final List<Expr> mList;
}
