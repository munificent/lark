package com.stuffwithstuff.lark;

import java.util.*;

public class Interpreter {

    public Interpreter() {
        mGlobal = new Environment(null);
    }
    
    public Expr eval(Expr expr) {
        return eval(mGlobal, expr);
    }
    
    private Expr eval(Environment scope, Expr expr) {
        //### bob: instanceof here is lame...
        if (expr.isLiteral()) {
            // literals, by definition, evaluate to themselves
            return expr;
            
        } else if (expr instanceof NameExpr) {

            // look up a name in the environment
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
    
    private Expr apply(Environment scope, Expr functionExpr, Expr argExpr) {
        // handle special forms
        if (functionExpr instanceof NameExpr) {
            String name = ((NameExpr)functionExpr).getName();
            
            if (name.equals("'")) return evalQuote(scope, argExpr);
            if (name.equals("do")) return evalDo(scope, argExpr);
            if (name.equals("print")) return evalPrint(scope, argExpr);
            if (name.equals("=>")) return evalLambda(scope, argExpr);
            if (name.equals("def:is:")) return evalDefIs(scope, argExpr);
        }
        
        // if we got here, it isn't a special form, so evaluate it normally
        Expr function = eval(scope, functionExpr);
        Expr arg      = eval(scope, argExpr);
        
        //### bob: need error-handling
        // must be a function
        if (!(function instanceof FunctionExpr)) {
            return new NameExpr("called object is not a function");
        }
        
        return call((FunctionExpr)function, scope, arg);
    }
    
    private Expr evalQuote(Environment scope, Expr argExpr) {
        // a quote just evaluates to its (unevaluated) argument
        // 'a -> a
        return argExpr;
    }
    
    private Expr evalDo(Environment scope, Expr argExpr) {
        // if the arg isn't a list, just eval it normally
        if (!(argExpr instanceof ListExpr)) return eval(scope, argExpr);
        
        // evaluate each item in the arg list in order, returning the last one
        ListExpr argList = (ListExpr)argExpr;
        
        Expr result = null;
        for (Expr arg : argList.getList()) {
            result = eval(scope, arg);
        }
        
        return result;
    }
    
    private Expr evalPrint(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        System.out.println(arg);

        return Expr.unit();
    }

    private Expr evalLambda(Environment scope, Expr arg) {
        // a => (a * a)
        
        //### bob: need lots of error-handling here
        ListExpr argList = (ListExpr)arg;
        
        // get the parameter name(s)
        List<String> paramNames = new ArrayList<String>();
        Expr parameters = argList.getList().get(0);
        if (parameters instanceof ListExpr) {
            ListExpr paramList = (ListExpr)parameters;
            
            for (Expr param : paramList.getList()) {
                paramNames.add(((NameExpr)param).getName());
            }
        } else {
            // not a list, so assume it's a single name
            paramNames.add(((NameExpr)parameters).getName());
        }
        
        // create the function
        return new FunctionExpr(paramNames, argList.getList().get(1));
        //### bob: need to support closures at some point
    }
    
    private Expr evalDefIs(Environment scope, Expr arg) {
        //### bob: need lots of error-checking all through here!
        ListExpr args = (ListExpr)arg;
        
        String name = ((NameExpr)args.getList().get(0)).getName();
        Expr body = args.getList().get(1);
        
        // define the name in the current scope
        scope.put(name, eval(scope, body));
        
        return Expr.unit();
    }

    private Expr call(FunctionExpr function, Environment parentScope, Expr arg) {
        List<String> params = function.getParameters();
        
        //### bob: need better error-handling
        // make sure we have the right number of arguments
        if (params.size() == 0) {
            if (!(arg instanceof ListExpr)) return new NameExpr("expected no args but got one");
            if (((ListExpr)arg).getList().size() != 0) return new NameExpr("expected no arg but got some");
        } else if (params.size() > 1) {
            if (!(arg instanceof ListExpr)) return new NameExpr("expected multiple args but got one");
            if (((ListExpr)arg).getList().size() != params.size()) return new NameExpr("got wrong number of arguments");
        }
        
        // create a new local scope for the function
        Environment scope = parentScope.create();
        
        // bind the arguments to the parameters
        if (params.size() == 1) {
            scope.put(params.get(0), arg);
        } else if (params.size() > 1) {
            ListExpr args = (ListExpr)arg;
            for (int i = 0; i < params.size(); i++) {
                scope.put(params.get(i), args.getList().get(i));
            }
        }
        
        // evaluate the body in the new scope
        return eval(scope, function.getBody());
    }

    /*
    private Expr_Old eval(Environment scope, Expr_Old expr) {
        // literals, by definition, evaluate to themselves
        if (expr.isLiteral()) return expr;
        
        // special forms
        if (expr.getName().equals("def:is:"))    return evalDefIs(scope, expr);
        if (expr.getName().equals("def:is:in:")) return evalDefIsIn(scope, expr);
        
        // look up the name in the current scope
        Expr_Old result = scope.get(expr.getName());
        
        // if arguments are provided, apply them
        if (expr.size() > 0) {
            result = apply(scope, result, expr);
        }

        return result;
    }
    
    private Expr_Old apply(Environment scope, Expr_Old function, Expr_Old call) {
        // a => (a + a)
        
        //### bob: need error-handling
        if (!function.getName().equals("fn:body:")) return new Expr_Old(new Atom("not a function."));
        if (function.size() != 2) return new Expr_Old(new Atom("function definition doesn't have right number of args."));

        // get the formal parameters of the function
        Expr_Old paramExpr = function.get(0);

        // make sure we have the right number of arguments
        //### bob: need error-handling
        if (paramExpr.size() != call.size()) return new Expr_Old(new Atom("wrong number of args."));
        
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
    
    private Expr_Old evalDefIsIn(Environment scope, Expr_Old expr) {
        String name = expr.get(0).getName();
        
        // create a new lexical scope with the name bound in it
        Environment childScope = scope.create();
        childScope.put(name, eval(scope, expr.get(1)));
        
        // evaluate the body of the let
        return eval(childScope, expr.get(2));
    }
        */

    private final Environment mGlobal;
}
