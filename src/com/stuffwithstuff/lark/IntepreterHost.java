package com.stuffwithstuff.lark;

public interface IntepreterHost {
    void print(final String text);
    void error(final String text);
    void warning(final String text);
}
