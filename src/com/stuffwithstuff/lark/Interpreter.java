package com.stuffwithstuff.lark;

public class Interpreter {

    public Interpreter() {
        mGlobal = new Environment(null);
    }
    
    public Expr eval(Expr expr) {
        // start at the global scope
        return eval(mGlobal, expr);
    }
    
    private Expr eval(Environment scope, Expr expr) {
        // literals, by definition, evaluate to themselves
        if (expr.isLiteral()) return expr;
        
        // special forms
        if (expr.getName().equals("def:is:"))    return evalDefIs(scope, expr);
        if (expr.getName().equals("def:is:in:")) return evalDefIsIn(scope, expr);
        
        // look up the name in the current scope
        Expr result = scope.get(expr.getName());
        
        // if arguments are provided, apply them
        if (expr.size() > 0) {
            result = apply(scope, result, expr);
        }

        return result;
    }
    
    private Expr apply(Environment scope, Expr function, Expr call) {
        // fn: [a] body: a + a
        
        //### bob: need error-handling
        if (!function.getName().equals("fn:body:")) return new Expr(new Atom("not a function."));
        if (function.size() != 2) return new Expr(new Atom("function definition doesn't have right number of args."));

        // get the formal parameters of the function
        Expr paramExpr = function.get(0);

        // make sure we have the right number of arguments
        //### bob: need error-handling
        if (paramExpr.size() != call.size()) return new Expr(new Atom("wrong number of args."));
        
        // create a local scope for the function
        Environment local = scope.create();
        
        // evaluate the arguments and bind to the parameters
        for (int i = 0; i < paramExpr.size(); i++) {
            local.put(paramExpr.get(i).getName(),
                      eval(scope, call.get(i)));
        }
        
        // evaluate the body of the function
        return eval(local, function.get(1));
    }
    
    private Expr evalDefIs(Environment scope, Expr expr) {
        String name = expr.get(0).getName();
        
        // define the name in the current scope
        scope.put(name, eval(scope, expr.get(1)));
        
        return Expr.unit();
    }

    private Expr evalDefIsIn(Environment scope, Expr expr) {
        String name = expr.get(0).getName();
        
        // create a new lexical scope with the name bound in it
        Environment childScope = scope.create();
        childScope.put(name, eval(scope, expr.get(1)));
        
        // evaluate the body of the let
        return eval(childScope, expr.get(2));
    }
    
    private final Environment mGlobal;
}
