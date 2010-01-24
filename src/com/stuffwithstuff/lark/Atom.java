package com.stuffwithstuff.lark;

public class Atom {
    
    public Atom() {
        mType = AtomType.UNIT;
        mName = "";
        mInt  = 0;
    }
    
    public Atom(final String name) {
        mType = AtomType.NAME;
        mName = name;
        mInt  = 0;
    }

    public Atom(final int value) {
        mType = AtomType.INT;
        mName = "";
        mInt  = value;
    }
    
    public AtomType getType() {
        return mType;
    }
    
    public String getName() {
        //### bob: should check that type is name
        return mName;
    }
    
    public int getInt() {
        //### bob: should check that type is int
        return mInt;
    }
    
    @Override
    public String toString() {
        switch (mType) {
        case UNIT: return "()";
        case NAME: return mName;
        case INT:  return Integer.toString(mInt);
        }
        
        return "unknown atom type";
    }

    private final AtomType mType;
    private final String mName;
    private final int    mInt;

}
