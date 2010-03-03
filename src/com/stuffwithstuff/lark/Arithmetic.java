package com.stuffwithstuff.lark;

/**
 * Built-in arithmetic functions.
 */
public class Arithmetic {

    public static CallableExpr add() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "+", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new NumExpr(((NumExpr)argList.getList().get(0)).getValue() +
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr subtract() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "-", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new NumExpr(((NumExpr)argList.getList().get(0)).getValue() -
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr multiply() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "*", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new NumExpr(((NumExpr)argList.getList().get(0)).getValue() *
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr divide() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "/", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new NumExpr(((NumExpr)argList.getList().get(0)).getValue() /
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr equals() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                //### bob: need to handle strings and bools
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "=", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() ==
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr notEquals() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                //### bob: need to handle strings and bools
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "!=", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() !=
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr lessThan() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "<", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() <
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr greaterThan() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, ">", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() >
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr lessThanOrEqual() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, "<=", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() <=
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }

    public static CallableExpr greaterThanOrEqual() {
        return new CallableExpr() {
            public Expr call(Interpreter interpreter, Scope scope, Expr argExpr) {
                Expr arg = interpreter.eval(scope, argExpr);
                Expr error = validateBinaryArg(interpreter, ">=", arg);
                if (error != null) return error;
                
                ListExpr argList = (ListExpr)arg;
                
                return new BoolExpr(((NumExpr)argList.getList().get(0)).getValue() >=
                                   ((NumExpr)argList.getList().get(1)).getValue());
            }
        };
    }
   
    private static Expr validateBinaryArg(Interpreter interpreter, String op, Expr arg) {
        if (arg.getType() != ExprType.LIST) return interpreter.error("'" + op + "' requires two arguments.");
        ListExpr argList = (ListExpr)arg;
        
        if (argList.getList().size() != 2) return interpreter.error("'" + op + "' requires two arguments.");
        
        if (argList.getList().get(0).getType() != ExprType.NUMBER) return interpreter.error("'" + op + "' expects numeric arguments.");
        if (argList.getList().get(1).getType() != ExprType.NUMBER) return interpreter.error("'" + op + "' expects numeric arguments.");
        
        return null;
    }
}
