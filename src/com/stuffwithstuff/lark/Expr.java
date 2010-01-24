package com.stuffwithstuff.lark;

import java.util.*;

public class Expr {

    public Expr(final Collection<Expr> exprs) {
        mType = AtomType.None;
        mName = "";
        mInt  = 0;
        mExprs = new ArrayList<Expr>(exprs);
    }

    public Expr(final String name) {
        mType = AtomType.Name;
        mName = name;
        mInt  = 0;
        mExprs = new ArrayList<Expr>();
    }

    public Expr(final int value) {
        mType = AtomType.Int;
        mName = "";
        mInt  = value;
        mExprs = new ArrayList<Expr>();
    }
    
    public Expr(final String name, final Collection<Expr> exprs) {
        mType = AtomType.Name;
        mName = name;
        mInt  = 0;
        mExprs = new ArrayList<Expr>(exprs);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        // special case the unit expr
        if ((mType == AtomType.None) && (mExprs.size() == 0)) return "()";
        
        // add the atom
        switch (mType) {
        case Name: builder.append(mName); break;
        case Int:  builder.append(mInt); break;
        }
        
        // add the child expressions, if any
        if (mExprs.size() > 0) {
            builder.append("(");
            
            for (int i = 0; i < mExprs.size(); i++) {
                builder.append(mExprs.get(i).toString());
                if (i < mExprs.size() - 1) builder.append(", ");
            }
            
            builder.append(")");
        }
        
        return builder.toString();
    }
    
    private enum AtomType {
        None, Name, Int
    }
    
    private final AtomType mType;
    private final String mName;
    private final int    mInt;
    private final ArrayList<Expr> mExprs;
}
