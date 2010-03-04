package com.stuffwithstuff.lark;

import java.io.*;

/**
 * Main entry point class for running either a single Lark script, or the
 * interactive REPL.
 */
public class Lark {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runRepl();
        } else if (args.length == 1) {
            if (args[0].equals("-t")) {
                TestRunner runner = new TestRunner();
                runner.run();
            } else {
                LarkScript.run(args[0]);
            }
        } else {
            System.out.println("Lark expects zero or one argument.");            
        }
    }

    private static void runRepl() throws IOException {
        System.out.println("lark v0.0.0");
        System.out.println("-----------");
        System.out.println("Type 'q' and press Enter to quit.");
        
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);

        Interpreter interpreter = new Interpreter();
        
        // load the base scripts
        //### bob: hack. assumes relative path.
        LarkScript base = new LarkScript("base/init.lark");
        base.run(interpreter);
        
        while (true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line.equals("q")) break;
            
            Lexer lexer = new Lexer(line);
            LarkParser parser = new LarkParser(lexer);
            
            try {
                Expr expr = parser.parse();
                Expr result = interpreter.eval(expr);
                
                System.out.println("= " + result.toString());
            } catch(ParseException ex) {
                System.out.println("Could not parse expression: " + ex.getMessage());
            }
        }
    }
}
