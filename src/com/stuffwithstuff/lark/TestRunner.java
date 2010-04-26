package com.stuffwithstuff.lark;

import java.util.*;
import java.io.*;

public class TestRunner {
    public void run() {
        System.out.println("running test suite...");
        System.out.println();
        
        File testDir = new File("test");
        
        for (File script : testDir.listFiles()) {
            if (script.getPath().endsWith(".lark")) {
                runTest(script);
            }
        }
        
        System.out.println();
        System.out.printf("%d out of %d tests passed", mPasses, mTests);
    }
    
    private void runTest(File path) {
        mOutput = 0;
        System.out.println(path.toString());
        
        try {            
            LarkScript script = new LarkScript(path.getPath());
            
            mPassed = true;
            
            // parse the script to get the expected behavior
            mExpectedOutput = new LinkedList<String>();
            String expectedResult = "";
            
            for (String line : script.getSource().split("\r\n|\r|\n")) {
                if (line.contains(OUTPUT_PREFIX)) {
                    int start = line.indexOf(OUTPUT_PREFIX) + OUTPUT_PREFIX.length();
                    mExpectedOutput.add(line.substring(start));
                }
                else if (line.contains(RESULT_PREFIX)) {
                    int start = line.indexOf(RESULT_PREFIX) + RESULT_PREFIX.length();
                    expectedResult = line.substring(start);
                }
            }
            
            Interpreter interpreter = new Interpreter(new TestHost());
            
            // load the base script
            //### bob: hack. assumes relative path.
            LarkScript base = new LarkScript("base/init.lark");
            base.run(interpreter);

            // run the script
            mRunning = true;
            Expr resultExpr = script.run(interpreter);
            mRunning = false;
            
            // check the result
            if (resultExpr == null) {
                System.out.println("- fail: got null expression");
                mPassed = false;
                
            } else if ((expectedResult.length() > 0) && !expectedResult.equals(resultExpr.toString())) {
                System.out.println("- fail: result was '" + resultExpr.toString() + "', expected '" + expectedResult + "'");
                mPassed = false;
            }
            
            // see if we missed output
            for (String expected : mExpectedOutput) {
                System.out.println("- fail: expected '" + expected + "' but got nothing");
            }
        }
        catch (IOException ex) {
            System.out.println("- fail: got exception loading test script");
            mPassed = false;
        }
        
        System.out.println("- passed " + mOutput + " lines of output");
        
        mTests++;
        if (mPassed) mPasses++;
    }
    
    private class TestHost implements IntepreterHost {
        @Override
        public void print(final String text) {
            if (mRunning) {
                if (mExpectedOutput.size() == 0) {
                    System.out.println("- fail: got '" + text + "' output when no more was expected");
                    mPassed = false;
                } else { 
                    String actual = mExpectedOutput.poll();
                    if (!actual.equals(text)) {
                        System.out.println("- fail: got '" + text + "' output when '" + actual + "' was expected");
                        mPassed = false;
                    } else {
                        mOutput++;
                    }
                }
            } else {
                System.out.println(text);
            }
        }
        
        @Override
        public void error(final String text) {
            if (!mRunning) return;
            
            System.out.println("- fail: got unexpected error '" + text + "'");
            mPassed = false;
        }
        
        @Override
        public void warning(final String text) {
            // ignore warnings
        }
    }
    
    private static final String OUTPUT_PREFIX = "# output: ";
    private static final String RESULT_PREFIX = "# result: ";
    
    private LinkedList<String> mExpectedOutput;
    private boolean mRunning;
    private boolean mPassed;
    private int mTests;
    private int mPasses;
    private int mOutput;
}
