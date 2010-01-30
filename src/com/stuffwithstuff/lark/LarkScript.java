package com.stuffwithstuff.lark;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

public class LarkScript {
    public static String run(String path) {
        try {
            LarkScript script = new LarkScript(path);
            Expr result = script.run();
            
            if (result == null) return "";
            return result.toString();
        }
        catch (FileNotFoundException ex) {
            System.out.println("Could not find the file '" + path + "'.");
        }
        catch (IOException ex) {
            System.out.println("Could not read the file '" + path + "'.");
        }
        
        return null;
    }
    
    public LarkScript(String path) throws IOException {
        mPath = path;
        mSource = readFile(path);
    }
    
    public String getPath() { return mPath; }
    public String getSource() { return mSource; }
    
    public Expr run(Printable printable) {
        if (mSource.length() > 0) {
            Lexer lexer = new Lexer(mSource);
            LarkParser parser = new LarkParser(lexer);
            Interpreter interpreter = new Interpreter(printable);

            Expr expr = parser.parse();
            
            if (expr != null) {
                // the body of a script is implicitly wrapped in a 'do'
                // so that the result is the last expression in the script
                expr = new CallExpr(new NameExpr("do"), expr);
                
                return interpreter.eval(expr);
            } else {
                System.out.println("Error parsing '" + mPath + "'.");
            }
        }
        
        return null;
    }
    
    public Expr run() {
        return run((Printable)null);
    }

    private static String readFile(String path) throws IOException {
        FileReader reader = new FileReader(path);
        StringBuilder builder = new StringBuilder();
        
        CharBuffer buffer = CharBuffer.allocate(1024);
        while (reader.read(buffer) != -1) {
            buffer.rewind();
            builder.append(buffer);
        }
        
        return builder.toString();
    }

    private final String mPath;
    private final String mSource;
}
