package com.stuffwithstuff.lark;

import java.util.*;

public class Interpreter {

    public Interpreter() {
        mGlobal = new Scope(null);
        
        // register the special forms
        mGlobal.put("'", SpecialForms.quote());
        mGlobal.put("do", SpecialForms.doForm());
        mGlobal.put("print", SpecialForms.print());
        mGlobal.put("=>", SpecialForms.createFunction());
        mGlobal.put("=>>", SpecialForms.createMacro());
        mGlobal.put("def:is:", SpecialForms.defIs());
        
        mGlobal.put("if:then:", SpecialForms.ifThen());
        mGlobal.put("if:then:else:", SpecialForms.ifThenElse());

        mGlobal.put("bool?", SpecialForms.boolPredicate());
        mGlobal.put("int?",  SpecialForms.intPredicate());
        mGlobal.put("list?", SpecialForms.listPredicate());
        mGlobal.put("name?", SpecialForms.namePredicate());
        mGlobal.put("unit?", SpecialForms.unitPredicate());

        mGlobal.put("count", SpecialForms.count());

        mGlobal.put("+", Arithmetic.add());
        mGlobal.put("-", Arithmetic.subtract());
        mGlobal.put("*", Arithmetic.multiply());
        mGlobal.put("/", Arithmetic.divide());
    }
    
    public Expr eval(Expr expr) {
        return eval(mGlobal, expr);
    }
    
    public Expr eval(Scope scope, Expr expr) {
        //### bob: instanceof here is lame...
        if (expr.isLiteral()) {
            // literals, by definition, evaluate to themselves
            return expr;
            
        } else if (expr instanceof NameExpr) {

            // look up a name in the scope
            return scope.get(((NameExpr)expr).getName());
            
        } else if (expr instanceof ListExpr) {
            
            ListExpr list = (ListExpr)expr;
            
            // evaluate each of the items
            List<Expr> results = new ArrayList<Expr>();
            for (Expr listExpr : list.getList()) {
                results.add(eval(scope, listExpr));
            }
            
            return new ListExpr(results);
            
        } else {
            CallExpr call = (CallExpr)expr;
            return apply(scope, call.getLeft(), call.getRight());
        }
    }
    
    public Expr error(String message) {
        System.out.println("! " + message);
        return Expr.unit();
    }
    
    private Expr apply(Scope scope, Expr functionExpr, Expr argExpr) {
        // evaluate the expression for the function we're calling
        Expr function = eval(scope, functionExpr);
                
        // must be callable
        if (!(function instanceof CallableExpr)) {
            return error("Called object is not callable.");
        }
        
        return ((CallableExpr)function).call(this, scope, argExpr);
    }
    
    private final Scope mGlobal;
}
