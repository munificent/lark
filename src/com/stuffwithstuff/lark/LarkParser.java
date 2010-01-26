package com.stuffwithstuff.lark;

import java.util.*;

public class LarkParser extends Parser {
    
    //### bob: other stuff to support
    // keywords that follow first arg?
    //   a b: c
    // [] for s-expr-style lists
    // ; for list with lower precendence than keyword and then make , higher?
    // {} for list where items are separated by newlines?
    // (+) for creating name from operator
    // (foo:) for creating name from keyword
    // strings
    // negative numbers and decimals
    
    
    //### bob: fix:
    // > (a, b) c
    // : ((,) (a, b, c))
    // doesn't look right. should be:
    // : (a, b) c
    
    public LarkParser(Lexer lexer) {
        super(lexer);
    }
    
    public Expr parse() {
        return list();
    }
    
    private Expr list() {
        List<Expr> exprs = new ArrayList<Expr>();
        exprs.add(keyword());
        
        while (match(TokenType.COMMA)) {
            exprs.add(keyword());
        }
        
        return new ListExpr(exprs);
    }
    
    private Expr keyword() {
        if (!isMatch(TokenType.KEYWORD)) return operator();

        List<String> keywords = new ArrayList<String>();
        List<Expr> args = new ArrayList<Expr>();

        while (match(TokenType.KEYWORD)) {
            keywords.add(getMatch()[0].getString());
            args.add(operator());
        }

        return new CallExpr(new NameExpr(join(keywords)), new ListExpr(args));
    }

    private Expr operator() {
        Expr syntax = call();

        while (match(TokenType.OPERATOR)) {
            String op = getMatch()[0].getString();
            Expr right = call();

            List<Expr> args = new ArrayList<Expr>();
            args.add(syntax);
            args.add(right);
            
            syntax = new CallExpr(new NameExpr(op), new ListExpr(args));
        }

        return syntax;        
    }
    
    private Expr call() {
        Expr left = primaryOrNull();
        
        //### bob: need error-handling
        if (left == null) return new NameExpr("parse error, expected primary");
        
        while (true) {
            Expr right = primaryOrNull();
            if (right == null) break;
            
            left = new CallExpr(left, right);
        }
        
        return left;
    }
    
    private Expr primaryOrNull() {
        if (match(TokenType.NAME)) {
            return new NameExpr(getMatch()[0].getString());
            
        } else if (match(TokenType.NUMBER)) {
            return new IntExpr(getMatch()[0].getInt());
            
        } else if (match(TokenType.LEFT_PAREN)) {
            // () is unit
            if (match(TokenType.RIGHT_PAREN)) {
                return Expr.unit();
            }
            
            Expr expr = list();
            
            if (!match(TokenType.RIGHT_PAREN)) {
                // no closing )
                //### bob: need error-handling!
                return new NameExpr("missing closing )!");
            }
            
            return expr;
        }
        
        return null;
    }
    
    /*
    public Expr_Old parse() {
        List<Expr_Old> exprs = sequence();
        
        // should be at end of file when parse completes
        if (!match(TokenType.EOF)) return null;
        
        return flatten(exprs);
    }
    
    private List<Expr_Old> sequence() {
        List<Expr_Old> exprs = new ArrayList<Expr_Old>();
        exprs.add(keyword());
        
        while (match(TokenType.COMMA)) {
            exprs.add(keyword());
        }
        
        return exprs;
    }
    
    private Expr_Old call() {
        List<Expr_Old> primary = primary(true);
        
        if (primary == null) {
            //### bob: temp hack. should throw exception or something
            return new Expr_Old(new Atom("HACK COULDN'T PARSE PRIMARY"));
        }
        
        return flatten(primary);
    }

    private List<Expr_Old> primary(boolean recurse) {
        List<Expr_Old> exprs = new ArrayList<Expr_Old>();

        if (match(TokenType.NAME)) {
            String name = getMatch()[0].getString();
            Atom atom = new Atom(name);
            
            if (recurse) {
                List<Expr_Old> args = primary(recurse);
                if (args == null) args = new ArrayList<Expr_Old>(); // a -> a ()
                exprs.add(new Expr_Old(atom, args));
            } else {
                exprs.add(new Expr_Old(atom));
            }
        } else if (match(TokenType.NUMBER)) {
            exprs.add(new Expr_Old(new Atom(getMatch()[0].getInt())));
        } else if (match(TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN)) {
            // the unit expr
            exprs.add(Expr_Old.unit());
        } else if (match(TokenType.LEFT_PAREN)) {
            exprs.addAll(sequence());
            if (!match(TokenType.RIGHT_PAREN)) {
                // ### bob: hack. should throw
                // throw new Exception("Missing closing ')'.");
            }
        } else if (match(TokenType.LEFT_BRACKET)) {
            exprs.add(bracketList());
            if (!match(TokenType.RIGHT_BRACKET)) {
                // ### bob: hack. should throw
                // throw new Exception("Missing closing ')'.");
            }
        } else {
            // bad parse
            return null;
        }

        return exprs;        
    }
    
    private Expr_Old bracketList() {
        List<Expr_Old> exprs = new ArrayList<Expr_Old>();
        
        // parse sequential primary expressions and build a single list
        while (!isMatch(TokenType.RIGHT_BRACKET)) {
            exprs.add(flatten(primary(false)));
        }
        
        // now build an expr from the results
        // note that we aren't calling flatten() here because we *don't* want
        // to turn a single element list into just that element, we always want
        // to wrap it in a new Expr.
        return new Expr_Old(exprs);
    }
    */
    
    private String join(Collection<?> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
        }
        return builder.toString();
    }
}
