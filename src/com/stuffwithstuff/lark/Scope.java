package com.stuffwithstuff.lark;

import java.util.Hashtable;

/**
 * Represents a name scope, i.e. an environment where names can be defined and
 * looked up.
 */
public class Scope {

    public Scope(final Scope parent) {
        mParent = parent;
        mBound = new Hashtable<String, Expr>();
    }
    
    /**
     * Creates a new child scope of this scope.
     */
    public Scope create() {
        return new Scope(this);
    }
    
    /**
     * Gets the value bound to the given name in this scope, or any of its
     * parent scopes.
     * 
     * @param name - the name of the value to look up.
     * @return the value bound to that name or null if not found.
     */
    public Expr get(String name) {
        // look it up in the current scope
        if (mBound.containsKey(name)) {
            return mBound.get(name);
        }
        
        // if we're at the global scope and haven't found it, it isn't defined
        if (mParent == null) return null;
        
        // walk up the scope chain
        return mParent.get(name);
    }
    
    public void put(String name, Expr value) {
        mBound.put(name, value);
    }
    
    private final Scope mParent;
    private final Hashtable<String, Expr> mBound;
}
