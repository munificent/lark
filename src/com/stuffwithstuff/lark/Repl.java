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

        while (true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line.equals("q")) break;
            
            Lexer lexer = new Lexer(line);
            LarkParser parser = new LarkParser(lexer);
            
            Expr expr = parser.parse();
            System.out.println("= " + expr.toString());
        }
    }

}
