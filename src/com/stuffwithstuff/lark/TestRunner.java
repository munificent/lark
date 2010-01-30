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
        System.out.println(path.toString());
        
        try {
            LarkScript script = new LarkScript(path.getPath());
            
            mPassed = true;
            
            // parse the script to get the expected behavior
            mExpectedOutput = new LinkedList<String>();
            String expectedResult = "";
            
            for (String line : script.getSource().split("\r\n|\r|\n")) {
                if (line.startsWith(OUTPUT_PREFIX)) {
                    mExpectedOutput.add(line.substring(OUTPUT_PREFIX.length()));
                }
                else if (line.startsWith(RESULT_PREFIX)) {
                    expectedResult = line.substring(RESULT_PREFIX.length());
                }
            }
            
            // run the script
            Expr resultExpr = script.run(new ExpectedPrintable());
            if (resultExpr == null) {
                System.out.println("- fail: got null expression");
                mPassed = false;
                
            } else if ((expectedResult.length() > 0) && !expectedResult.equals(resultExpr.toString())) {
                System.out.println("- fail: result was '" + resultExpr.toString() + "', expected '" + expectedResult + "'");
                mPassed = false;
            }
        }
        catch (IOException ex) {
            System.out.println("- fail: got exception loading test script");
            mPassed = false;
        }
        
        mTests++;
        if (mPassed) mPasses++;
    }
    
    private class ExpectedPrintable implements Printable {

        @Override
        public void print(String text) {
            if (mExpectedOutput.size() == 0) {
                System.out.println("- fail: got '" + text + "' output when no more was expected");
                mPassed = false;
            } else { 
                String actual = mExpectedOutput.poll();
                if (!actual.equals(text)) {
                    System.out.println("- fail: got '" + text + "' output when '" + actual + "' was expected");
                    mPassed = false;
                }
            }
        }
    }
    
    private static final String OUTPUT_PREFIX = "# output: ";
    private static final String RESULT_PREFIX = "# result: ";
    
    private LinkedList<String> mExpectedOutput;
    private boolean mPassed;
    private int mTests;
    private int mPasses;
}
