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
                
                return new IntExpr(((IntExpr)argList.getList().get(0)).getValue() +
                                   ((IntExpr)argList.getList().get(1)).getValue());
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
                
                return new IntExpr(((IntExpr)argList.getList().get(0)).getValue() -
                                   ((IntExpr)argList.getList().get(1)).getValue());
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
                
                return new IntExpr(((IntExpr)argList.getList().get(0)).getValue() *
                                   ((IntExpr)argList.getList().get(1)).getValue());
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
                
                return new IntExpr(((IntExpr)argList.getList().get(0)).getValue() /
                                   ((IntExpr)argList.getList().get(1)).getValue());
            }
        };
    }
   
    private static Expr validateBinaryArg(Interpreter interpreter, String op, Expr arg) {
        if (!(arg instanceof ListExpr)) return interpreter.error("'" + op + "' requires two arguments.");
        ListExpr argList = (ListExpr)arg;
        
        if (argList.getList().size() != 2) return interpreter.error("'" + op + "' requires two arguments.");
        
        if (!(argList.getList().get(0) instanceof IntExpr)) return interpreter.error("'" + op + "' expects numeric arguments.");
        if (!(argList.getList().get(1) instanceof IntExpr)) return interpreter.error("'" + op + "' expects numeric arguments.");
        
        return null;
    }
}
