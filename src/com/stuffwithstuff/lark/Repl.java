package com.stuffwithstuff.lark;

import java.io.*;

public class Repl {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        System.out.println("lark v0.0.0");
        System.out.println("-----------");
        System.out.println("Type 'q' and press Enter to quit.");
        
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);

        Interpreter interpreter = new Interpreter();
        
        while (true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line.equals("q")) break;
            
            Lexer lexer = new Lexer(line);
            LarkParser parser = new LarkParser(lexer);
            
            Expr expr = parser.parse();
            if (expr != null) {
                System.out.println(": " + expr.toString());
                
                Expr result = interpreter.eval(expr);
                System.out.println("= " + result.toString());
                                
            } else {
                System.out.println("! parse error");
            }
        }
    }

}
