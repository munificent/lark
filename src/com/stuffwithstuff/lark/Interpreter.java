package com.stuffwithstuff.lark;

import java.util.*;

public class Interpreter {
    public Interpreter() {
        this(null);
    }
    
    public Interpreter(IntepreterHost host) {
        if (host != null) {
            mHost = host;
        } else {
            mHost = new SysOutHost();
        }
        
        mGlobal = new Scope(null);
        
        // register the special forms
        mGlobal.put("'", SpecialForms.quote());
        mGlobal.put("eval", SpecialForms.eval());
        mGlobal.put("do", SpecialForms.doForm());
        mGlobal.put("print", SpecialForms.print());
        mGlobal.put("=>", SpecialForms.createFunction());
        mGlobal.put("=>>", SpecialForms.createMacro());
        mGlobal.put("def:is:", SpecialForms.defIs());
        mGlobal.put("global:is:", SpecialForms.globalIs());
        
        mGlobal.put("if:then:else:", SpecialForms.ifThenElse());

        mGlobal.put("bool?",   SpecialForms.boolPredicate());
        mGlobal.put("list?",   SpecialForms.listPredicate());
        mGlobal.put("name?",   SpecialForms.namePredicate());
        mGlobal.put("number?", SpecialForms.numberPredicate());
        mGlobal.put("string?", SpecialForms.stringPredicate());

        mGlobal.put("count", SpecialForms.count());

        mGlobal.put("+", Arithmetic.add());
        mGlobal.put("-", Arithmetic.subtract());
        mGlobal.put("*", Arithmetic.multiply());
        mGlobal.put("/", Arithmetic.divide());

        mGlobal.put("=", Arithmetic.equals());
        mGlobal.put("<", Arithmetic.lessThan());
        mGlobal.put(">", Arithmetic.greaterThan());
        mGlobal.put("<=", Arithmetic.lessThanOrEqual());
        mGlobal.put(">=", Arithmetic.greaterThanOrEqual());
    }
    
    public Scope getGlobalScope() {
        return mGlobal;
    }
    
    public Expr eval(Expr expr) {
        return eval(mGlobal, expr);
    }
    
    public Expr eval(Scope scope, Expr expr) {
        switch (expr.getType()) {
        case CALL:
            CallExpr call = (CallExpr)expr;

            // evaluate the expression for the function we're calling
            Expr function = eval(scope, call.getLeft());
            
            // must be callable
            if (!(function instanceof CallableExpr)) {
                return error("Called object is not callable.");
            }
            
            return ((CallableExpr)function).call(this, scope, call.getRight());
            
        case LIST:
            ListExpr list = (ListExpr)expr;
            
            // evaluate each of the items
            List<Expr> results = new ArrayList<Expr>();
            for (Expr listExpr : list.getList()) {
                results.add(eval(scope, listExpr));
            }
            
            return new ListExpr(results);
            
        case NAME:
            // look up a name in the scope
            String name = ((NameExpr)expr).getName();
            Expr value = scope.get(name);
            if (value == null) {
                warning("Could not find a value named '" + name + "'.");
                value = Expr.unit();
            }
            return value;
            
        default:
            // everything else is a literal, which evaluates to itself
            return expr;
        }
    }
    
    public Expr error(final String message) {
        mHost.error(message);
        return Expr.unit();
    }
    
    public void warning(final String message) {
        mHost.warning(message);
    }

    public void print(final String message) {
        mHost.print(message);
    }
    
    private static class SysOutHost implements IntepreterHost {
        public void print(final String text) {
            System.out.println(text);
        }

        public void error(final String text) {
            System.out.println("! " + text);
        }

        public void warning(final String text) {
            System.out.println("? " + text);
        }
    }
    
    private final Scope mGlobal;
    
    private IntepreterHost mHost;
}
