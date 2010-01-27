package com.stuffwithstuff.lark;

import java.io.*;
import java.nio.CharBuffer;

/**
 * Main entry point class for running either a single Lark script, or the
 * interactive REPL.
 */
public class Lark {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runRepl();
        } else if (args.length == 1) {
            runScript(args[0]);
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
        
        while (true) {
            System.out.print("> ");
            String line = in.readLine();
            if (line.equals("q")) break;
            
            Lexer lexer = new Lexer(line);
            LarkParser parser = new LarkParser(lexer);
            Expr expr = parser.parse();
            
            if (expr != null) {
                Expr result = interpreter.eval(expr);
                System.out.println("= " + result.toString());
            } else {
                System.out.println("! parse error");
            }
        }
    }

    private static void runScript(String path) {
        String source = readFile(path);
        
        if (source.length() > 0) {
            Lexer lexer = new Lexer(source);
            LarkParser parser = new LarkParser(lexer);
            Interpreter interpreter = new Interpreter();

            Expr expr = parser.parse();
            
            if (expr != null) {
                interpreter.eval(expr);
            } else {
                System.out.println("Error parsing '" + path + "'.");
            }
        }
    }
    
    private static String readFile(String path) {
        try {
            FileReader reader = new FileReader(path);
            StringBuilder builder = new StringBuilder();
            
            CharBuffer buffer = CharBuffer.allocate(1024);
            while (reader.read(buffer) != -1) {
                buffer.rewind();
                builder.append(buffer);
            }
            
            return builder.toString();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Could not find the file '" + path + "'.");
        }
        catch (IOException ex) {
            System.out.println("Could not read the file '" + path + "'.");
        }
        
        return "";
    }
}
