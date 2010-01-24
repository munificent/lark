package com.stuffwithstuff.lark;

import java.util.*;

public class Expr {

    public static Expr unit() {
        return new Expr(new Atom());
    }

    public Expr(final Collection<Expr> exprs) {
        mAtom = new Atom();
        mExprs = new ArrayList<Expr>(exprs);
    }

    public Expr(final Atom atom) {
        mAtom = atom;
        mExprs = new ArrayList<Expr>();
    }
    
    public Expr(final Atom atom, final Collection<Expr> exprs) {
        mAtom = atom;
        mExprs = new ArrayList<Expr>(exprs);
    }

    public boolean isLiteral() {
        return ((mAtom.getType() == AtomType.UNIT) && (mExprs.size() == 0)) ||
               (mAtom.getType() == AtomType.INT);
    }
    
    public String getName() {
        return mAtom.getName();
    }
    
    public int size() {
        return mExprs.size();
    }
    
    public Expr get(int index) {
        return mExprs.get(index);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        // add the atom
        builder.append(mAtom.toString());
        
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
    
    private final Atom mAtom;
    private final ArrayList<Expr> mExprs;
}
