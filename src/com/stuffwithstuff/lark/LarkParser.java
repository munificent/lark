package com.stuffwithstuff.lark;

import java.util.*;

public class LarkParser extends Parser {
    
    public LarkParser(Lexer lexer) {
        super(lexer);
    }
    
    public Expr parse() {
        List<Expr> exprs = sequence();
        
        // should be at end of file when parse completes
        if (!match(TokenType.EOF)) return null;
        
        return flatten(exprs);
    }
    
    private List<Expr> sequence() {
        List<Expr> exprs = new ArrayList<Expr>();
        exprs.add(keyword());
        
        while (match(TokenType.COMMA)) {
            exprs.add(keyword());
        }
        
        return exprs;
    }
    
    private Expr keyword() {
        if (!isMatch(TokenType.KEYWORD)) return operator();

        List<String> keywords = new ArrayList<String>();
        List<Expr> args = new ArrayList<Expr>();

        while (match(TokenType.KEYWORD)) {
            keywords.add(getMatch()[0].getString());
            args.add(operator());
        }

        return new Expr(join(keywords), args);
    }
    
    private Expr operator() {
        Expr expr = call();

        while (match(TokenType.OPERATOR)) {
            String op = getMatch()[0].getString();
            Expr right = call();

            List<Expr> args = new ArrayList<Expr>();
            args.add(expr);
            args.add(right);
            
            expr = new Expr(op, args);
        }

        return expr;
    }
    
    private Expr call() {
        List<Expr> primary = primary();
        
        if (primary == null) {
            //### bob: temp hack. should throw exception or something
            return new Expr("HACK COULDN'T PARSE PRIMARY");
        }
        
        return flatten(primary);
    }
    
    private List<Expr> primary() {
        List<Expr> exprs = new ArrayList<Expr>();

        if (match(TokenType.NAME))
        {
            String name = getMatch()[0].getString();

            List<Expr> args = primary();

            if (args == null) args = new ArrayList<Expr>(); // a -> a ()

            exprs.add(new Expr(name, args));
        }
        else if (match(TokenType.NUMBER))
        {
            exprs.add(new Expr(getMatch()[0].getInt()));
        }
        else if (match(TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN))
        {
            // do nothing, leave empty list
        }
        else if (match(TokenType.LEFT_PAREN))
        {
            exprs.addAll(sequence());
            if (!match(TokenType.RIGHT_PAREN)) {
                //### bob: hack. should throw
                //throw new Exception("Missing closing ')'.");
            }
        }
        else
        {
            // bad parse
            return null;
        }

        return exprs;        
    }

    private String join(Collection<?> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
        }
        return builder.toString();
    }

    private Expr flatten(List<Expr> exprs) {
        // if it's a single expression, just return it
        if (exprs.size() == 1) return exprs.get(0);
        
        return new Expr(exprs);
    }
}
