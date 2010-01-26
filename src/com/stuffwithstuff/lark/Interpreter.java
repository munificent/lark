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
        //### bob: this isn't exactly right. we aren't evaluating the function
        // expression before handling special forms. this is good because you
        // can't evaluate the args for special forms: "bool?" is not defined
        // in the current scope, so evaluating that name returns ().
        // it's bad in that it means you can't use an expression that *does*
        // evaluate to the name of a special form. ex:
        //
        // > (if: true then: print) 123
        //
        // this should print 123, but it won't. to fix this, what we probably
        // need to do is turn the special forms into special instance of
        // FunctionExpr. then, we can create those instances and actually bind
        // them to their names in the global scope:
        // mGlobal.put("bool?", new FunctionExpr(...));
        // then in apply, we can evaluate the function before handling special
        // forms.
        
        // handle special forms
        if (functionExpr instanceof NameExpr) {
            String name = ((NameExpr)functionExpr).getName();
            
            if (name.equals("'")) return evalQuote(scope, argExpr);
            if (name.equals("do")) return evalDo(scope, argExpr);
            if (name.equals("print")) return evalPrint(scope, argExpr);
            if (name.equals("=>")) return evalLambda(scope, argExpr);
            if (name.equals("def:is:")) return evalDefIs(scope, argExpr);

            if (name.equals("if:then:")) return evalIfThen(scope, argExpr);
            if (name.equals("if:then:else:")) return evalIfThenElse(scope, argExpr);

            if (name.equals("bool?")) return evalBoolPredicate(scope, argExpr);
            if (name.equals("int?")) return evalIntPredicate(scope, argExpr);
            if (name.equals("list?")) return evalListPredicate(scope, argExpr);
            if (name.equals("name?")) return evalNamePredicate(scope, argExpr);
            if (name.equals("unit?")) return evalUnitPredicate(scope, argExpr);
            
            // list operations
            if (name.equals("count")) return evalCount(scope, argExpr);
        }
        else if (functionExpr instanceof IntExpr) {
            // an int is a "function" that takes a list and returns the element
            // at that (zero-based) index in the list
            // > 1 (4, 5, 6)
            // = 5
            // > (1, 2, 3).2
            // = 3
            return evalIndex(scope, (IntExpr)functionExpr, argExpr);
        }
        
        // if we got here, it isn't a special form, so evaluate it normally
        Expr function = eval(scope, functionExpr);
        Expr arg = eval(scope, argExpr);
        
        // must be a function
        if (!(function instanceof FunctionExpr)) {
            return error("Called object is not a function.");
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
    
    private Expr evalIfThen(Environment scope, Expr argExpr) {
        if (!(argExpr instanceof ListExpr)) return error("'if:then:' expects an argument list.");
        
        ListExpr argListExpr = (ListExpr)argExpr;
        if (argListExpr.getList().size() != 2) return error ("'if:then:' expects two arguments.");
        
        // evaluate the condition
        Expr condition = eval(scope, argListExpr.getList().get(0));
        
        if (!(condition instanceof BoolExpr)) return error("'if:then:' condition must evaluate to true or false.");
        
        // evaluate the then branch 
        if (((BoolExpr)condition).getValue()) {
            return eval(scope, argListExpr.getList().get(1));
        } else {
            // condition was false
            return Expr.unit();
        }
    }
    
    private Expr evalIfThenElse(Environment scope, Expr argExpr) {
        if (!(argExpr instanceof ListExpr)) return error("'if:then:else:' expects an argument list.");
        
        ListExpr argListExpr = (ListExpr)argExpr;
        if (argListExpr.getList().size() != 3) return error ("'if:then:else:' expects three arguments.");
        
        // evaluate the condition
        Expr condition = eval(scope, argListExpr.getList().get(0));
        
        if (!(condition instanceof BoolExpr)) return error("'if:then:else:' condition must evaluate to true or false.");
        
        // evaluate the then branch 
        if (((BoolExpr)condition).getValue()) {
            return eval(scope, argListExpr.getList().get(1));
        } else {
            // condition was false
            return eval(scope, argListExpr.getList().get(2));
        }
    }

    private Expr call(FunctionExpr function, Environment parentScope, Expr arg) {
        List<String> params = function.getParameters();
        
        // make sure we have the right number of arguments
        if (params.size() == 0) {
            if (!(arg instanceof ListExpr)) return error("Function expects no arguments but got one.");
            if (((ListExpr)arg).getList().size() != 0) return error("Function expects no arguments but got multiple.");
        } else if (params.size() > 1) {
            if (!(arg instanceof ListExpr)) return new NameExpr("Function expects multiple arguments but got one.");
            if (((ListExpr)arg).getList().size() != params.size()) return error("Function did not get expected number of arguments.");
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
    
    private Expr evalBoolPredicate(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        return new BoolExpr(arg instanceof BoolExpr);
    }
    
    private Expr evalIntPredicate(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        return new BoolExpr(arg instanceof IntExpr);
    }
    
    private Expr evalListPredicate(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        return new BoolExpr(arg instanceof ListExpr);
    }
    
    private Expr evalNamePredicate(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        return new BoolExpr(arg instanceof NameExpr);
    }
    
    private Expr evalUnitPredicate(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        // unit is the empty list
        return new BoolExpr((arg instanceof ListExpr) &&
                (((ListExpr)arg).getList().size() == 0));
    }
    
    private Expr evalCount(Environment scope, Expr argExpr) {
        Expr arg = eval(scope, argExpr);
        
        if (!(arg instanceof ListExpr)) return error("Argument to 'count' must be a list.");
        
        ListExpr list = (ListExpr)arg;
        return new IntExpr(list.getList().size());
    }
    
    private Expr evalIndex(Environment scope, IntExpr index, Expr argExpr) {
        Expr arg = eval(scope, argExpr);

        if (!(arg instanceof ListExpr)) return error("Argument to index function must be a list.");
        
        ListExpr list = (ListExpr)arg;
        
        if (index.getValue() < 0) return error("Index must be non-negative.");
        if (index.getValue() >= list.getList().size()) return error("Index is out of bounds.");
        
        return list.getList().get(index.getValue());
    }

    private Expr error(String message) {
        System.out.println("! " + message);
        return Expr.unit();
    }
    
    private final Environment mGlobal;
}
