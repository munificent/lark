package com.stuffwithstuff.lark;

import java.util.*;

public class LarkParser extends Parser {
    public LarkParser(Lexer lexer) {
        super(lexer);
    }
    
    public Expr parse() throws ParseException {
        return semicolonList();
    }
    
    private Expr semicolonList() throws ParseException {
        List<Expr> exprs = new ArrayList<Expr>();
        
        do {
            // ignore trailing lines before closing a group
            if (isMatch(TokenType.RIGHT_PAREN)) break;
            if (isMatch(TokenType.RIGHT_BRACE)) break;
            if (isMatch(TokenType.RIGHT_BRACKET)) break;
            if (isMatch(TokenType.EOF)) break;
            
            exprs.add(keyword());
        } while(match(TokenType.LINE));
        
        // only create a list if we actually had a ;
        if (exprs.size() == 1) return exprs.get(0);

        return new ListExpr(exprs);        
    }
    
    private Expr keyword() throws ParseException {
        if (!isMatch(TokenType.KEYWORD)) return commaList();

        List<String> keywords = new ArrayList<String>();
        List<Expr> args = new ArrayList<Expr>();

        while (match(TokenType.KEYWORD)) {
            keywords.add(getMatch()[0].getString());
            args.add(commaList());
        }

        return new CallExpr(new NameExpr(join(keywords)), new ListExpr(args));
    }

    private Expr commaList() throws ParseException {
        List<Expr> exprs = new ArrayList<Expr>();
        
        do {
            exprs.add(operator());
        } while (match(TokenType.COMMA));
        
        // only create a list if we actually had a ,
        if (exprs.size() == 1) return exprs.get(0);
        
        return new ListExpr(exprs);
    }

    private Expr operator() throws ParseException {
        Expr expr = call();

        while (match(TokenType.OPERATOR)) {
            String op = getMatch()[0].getString();
            Expr right = call();

            List<Expr> args = new ArrayList<Expr>();
            args.add(expr);
            args.add(right);
            
            expr = new CallExpr(new NameExpr(op), new ListExpr(args));
        }

        return expr;        
    }
    
    private Expr call() throws ParseException {
        Stack<Expr> stack = new Stack<Expr>();
        
        // push as many calls as we can parse
        while (true) {
            Expr expr = dottedOrNull();
            if (expr == null) break;
            stack.push(expr);
        }
        
        if (stack.size() == 0) {
            throw new ParseException("Expected primary expression.");
        }
        
        // and then pop them back off to be right-associative
        Expr result = stack.pop();
        while (stack.size() > 0) {
            result = new CallExpr(stack.pop(), result);
        }
        
        return result;
    }

    private Expr dottedOrNull() throws ParseException {
        Expr expr = primaryOrNull();
        
        if (expr == null) return null;

        while (match(TokenType.DOT)) {
            Expr right = primaryOrNull();

            if (right == null) throw new ParseException("Expected expression after '.'");
            
            // swap the function and argument
            // a.b -> b(a)
            expr = new CallExpr(right, expr);
        }

        return expr;        
    }
    
    private Expr primaryOrNull() throws ParseException {
        if (match(TokenType.NAME)) {
            String name = getMatch()[0].getString();
            
            // check for reserved names
            if (name.equals("true")) return new BoolExpr(true);
            if (name.equals("false")) return new BoolExpr(false);
            
            return new NameExpr(name);
            
        } else if (match(TokenType.NUMBER)) {
            return new NumExpr(getMatch()[0].getDouble());
            
        } else if (match(TokenType.STRING)) {
            return new StringExpr(getMatch()[0].getString());
            
        } else if (match(TokenType.LEFT_PAREN)) {
            // () is unit
            if (match(TokenType.RIGHT_PAREN)) {
                return Expr.unit();
            }
            
            // handle (operator) and (keyword:) so that you can create
            // name exprs for operators and keywords without actually
            // having to parse them as used.
            if (match(TokenType.OPERATOR, TokenType.RIGHT_PAREN)) {
                return new NameExpr(getMatch()[0].getString());
            }
            if (match(TokenType.KEYWORD, TokenType.RIGHT_PAREN)) {
                return new NameExpr(getMatch()[0].getString());
            }
            
            Expr expr = semicolonList();
            
            if (!match(TokenType.RIGHT_PAREN)) throw new ParseException("Missing closing ')'.");
            
            return expr;
        } else if (match(TokenType.LEFT_BRACE)) {
            // { a } -> do a
            Expr expr = semicolonList();
            
            if (!match(TokenType.RIGHT_BRACE)) throw new ParseException("Missing closing '}'.");
            
            return new CallExpr(new NameExpr("do"), expr);
        } else if (match(TokenType.LEFT_BRACKET)) {
            
            List<Expr> exprs = new ArrayList<Expr>();
            
            while (true) {
                Expr term = primaryOrNull();
                if (term == null) break;
                
                // ignore lines in a [] expression
                match(TokenType.LINE);
                
                exprs.add(term);
            }
            
            if (!match(TokenType.RIGHT_BRACKET)) throw new ParseException("Missing closing ']'.");
            
            return new ListExpr(exprs);
        }
        
        return null;
    }
    
    private String join(Collection<?> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
        }
        return builder.toString();
    }
}
