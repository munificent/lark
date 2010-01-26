package com.stuffwithstuff.lark;

import java.util.*;


public abstract class Parser {
    public Parser(Lexer lexer) {
        mLexer = lexer;
        
        mRead = new LinkedList<Token>();
    }
    
    protected Token[] getMatch()
    {
        return mLastMatch;
    }
    
    protected boolean isMatch(TokenType... types) {
        for (int i = 0; i < types.length; i++) {
            if (!lookAhead(i).getType().equals(types[i])) return false;
        }

        return true;
    }
    
    protected boolean match(TokenType... types) {
        // don't consume any unless all match
        if (!isMatch(types)) return false;
        
        mLastMatch = new Token[types.length];
        
        for (int i = 0; i < mLastMatch.length; i++) {
            mLastMatch[i] = mRead.poll();
        }
        
        return true;
    }
    
    private Token lookAhead(int distance) {
        // read in as many as needed
        while (distance >= mRead.size()) {
            mRead.add(mLexer.readToken());
        }
        
        // get the queued token
        return mRead.get(distance);
    }
    
    private final Lexer mLexer;
    
    private final LinkedList<Token> mRead;
    private Token[] mLastMatch;
}
