package com.stuffwithstuff.lark;

import java.io.*;
import java.nio.charset.Charset;

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
    
    public Expr run(IntepreterHost printable) {
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
        return run((IntepreterHost)null);
    }
    
    private static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(path);
        
        try {
            InputStreamReader input = new InputStreamReader(stream, Charset.defaultCharset());
            Reader reader = new BufferedReader(input);
            
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            
            return builder.toString();
        } finally {
            stream.close();
        }
    }

    private final String mPath;
    private final String mSource;
}
