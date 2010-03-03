package com.stuffwithstuff.lark;

import java.util.ArrayList;
import java.util.List;

public class SpecialForms {

    public static CallableExpr quote() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                return argExpr;
            }
        };
    }

    public static CallableExpr eval() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                return interpreter.eval(scope, arg);
            }
        };
    }

    public static CallableExpr doForm() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                
                // create a new local scope for the body
                Scope local = scope.create();
                
                // if the arg isn't a list, just eval it normally
                if (argExpr.getType() != ExprType.LIST) return interpreter.eval(local, argExpr);
                
                // evaluate each item in the arg list in order, returning the last one
                ListExpr argList = (ListExpr)argExpr;
                
                Expr result = null;
                for (Expr arg : argList.getList()) {
                    result = interpreter.eval(local, arg);
                }
                
                return result;
            }
        };
    }

    public static CallableExpr print() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                interpreter.print(arg.toString());

                return Expr.unit();
            }
        };
    }

    public static CallableExpr createFunction() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                return createFunction(false, scope, argExpr);
            }
        };
    }
    
    public static CallableExpr createMacro() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                return createFunction(true, scope, argExpr);
            }
        };
    }
    
    public static CallableExpr defIs() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                return define(false, interpreter, scope, argExpr);
            }
        };
    }
    
    public static CallableExpr globalIs() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                return define(true, interpreter, scope, argExpr);
            }
        };
    }
    
    public static CallableExpr ifThen() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                if (argExpr.getType() != ExprType.LIST) return interpreter.error("'if:then:' expects an argument list.");
                
                ListExpr argListExpr = (ListExpr)argExpr;
                if (argListExpr.getList().size() != 2) return interpreter.error ("'if:then:' expects two arguments.");
                
                // evaluate the condition
                Expr condition = interpreter.eval(scope, argListExpr.getList().get(0));
                
                if (condition.getType() != ExprType.BOOL) return interpreter.error("'if:then:' condition must evaluate to true or false.");
                
                // evaluate the then branch 
                if (((BoolExpr)condition).getValue()) {
                    return interpreter.eval(scope, argListExpr.getList().get(1));
                } else {
                    // condition was false
                    return Expr.unit();
                }
            }
        };
    }
    
    public static CallableExpr ifThenElse() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                if (argExpr.getType() != ExprType.LIST) return interpreter.error("'if:then:else:' expects an argument list.");
                
                ListExpr argListExpr = (ListExpr)argExpr;
                if (argListExpr.getList().size() != 3) return interpreter.error ("'if:then:else:' expects three arguments.");
                
                // evaluate the condition
                Expr condition = interpreter.eval(scope, argListExpr.getList().get(0));
                
                if (condition.getType() != ExprType.BOOL) return interpreter.error("'if:then:else:' condition must evaluate to true or false.");
                
                // evaluate the then branch 
                if (((BoolExpr)condition).getValue()) {
                    return interpreter.eval(scope, argListExpr.getList().get(1));
                } else {
                    // condition was false
                    return interpreter.eval(scope, argListExpr.getList().get(2));
                }
            }
        };
    }
    
    public static CallableExpr boolPredicate() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                return new BoolExpr(arg.getType() == ExprType.BOOL);
            }
        };
    }
    
    public static CallableExpr listPredicate() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                return new BoolExpr(arg.getType() == ExprType.LIST);
            }
        };
    }
    
    public static CallableExpr namePredicate() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                return new BoolExpr(arg.getType() == ExprType.NAME);
            }
        };
    }
    
    public static CallableExpr numberPredicate() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                return new BoolExpr(arg.getType() == ExprType.NUMBER);
            }
        };
    }
    
    public static CallableExpr stringPredicate() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                return new BoolExpr(arg.getType() == ExprType.STRING);
            }
        };
    }

    public static CallableExpr count() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                
                if (arg.getType() != ExprType.LIST) return interpreter.error("Argument to 'count' must be a list.");
                
                ListExpr list = (ListExpr)arg;
                return new NumExpr(list.getList().size());
            }
        };
    }
    
    private static Expr define(boolean isGlobal, Interpreter interpreter, Scope scope, Expr argExpr) {
        if (!(argExpr instanceof ListExpr)) return interpreter.error("def:is: needs more than one argument.");
        
        ListExpr args = (ListExpr)argExpr;
        
        if (args.getList().size() != 2) return interpreter.error("def:is: expects two arguments.");
        
        // get the list of names being defined
        Expr nameArg = args.getList().get(0);
        List<String> names = new ArrayList<String>();
        if (nameArg.getType() == ExprType.NAME) {
            // defining a single name
            names.add(((NameExpr)nameArg).getName());
            
        } else if (nameArg.getType() == ExprType.LIST) {
            // defining a list of names
            ListExpr namesList = (ListExpr)nameArg;
            for (Expr name : namesList.getList()) {
                if (name.getType() != ExprType.NAME) {
                    return interpreter.error("First argument to def:is: must be a name, list, or call.");
                }
                names.add(((NameExpr)name).getName());
            }
        } else {
            return interpreter.error("First argument to def:is: must be a name, list, or call.");
        }
        
        // evaluate the value(s)
        Expr body = args.getList().get(1);
        Expr value = interpreter.eval(scope, body);
        
        // make sure the body matches the names
        if (names.size() > 1) {
            if (value.getType() != ExprType.LIST) return interpreter.error("When defining multiple names, the value must be a list.");
            ListExpr valueList = (ListExpr)value;
            if (names.size() != valueList.getList().size()) return interpreter.error("When defining multiple names, the number of names and values must match.");
        }
        
        // define the names in the correct scope
        if (names.size() == 1) {
            defineName(isGlobal, interpreter, scope, names.get(0), value);
        } else {
            ListExpr values = (ListExpr)value;
            for (int i = 0; i < names.size(); i++) {
                defineName(isGlobal, interpreter, scope, names.get(i), values.getList().get(i));
            }
        }
        
        return Expr.unit();
    }
    
    private static void defineName(boolean isGlobal, Interpreter interpreter, Scope scope, String name, Expr value) {
        if (isGlobal) {
            interpreter.getGlobalScope().put(name, value);
        } else {
            scope.put(name, value);            
        }        
    }
    
    private static Expr createFunction(boolean isMacro, Scope scope, Expr arg) {
        //### bob: need lots of error-handling here
        ListExpr argList = (ListExpr)arg;
        
        // get the parameter name(s)
        List<String> paramNames = new ArrayList<String>();
        Expr parameters = argList.getList().get(0);
        if (parameters.getType() == ExprType.LIST) {
            ListExpr paramList = (ListExpr)parameters;
            
            for (Expr param : paramList.getList()) {
                paramNames.add(((NameExpr)param).getName());
            }
        } else {
            // not a list, so assume it's a single name
            paramNames.add(((NameExpr)parameters).getName());
        }
        
        // create the function
        return new FunctionExpr(scope, isMacro, paramNames, argList.getList().get(1));
        //### bob: need to support closures at some point
    }
}
