package com.compiler.delphi;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Please provide a Delphi source file path");
            System.exit(1);
        }

        try {
            // Read the input file
            String input = Files.readString(Paths.get(args[0]));
            
            // Create a CharStream from the input
            CharStream charStream = CharStreams.fromString(input);
            
            // Create lexer
            DelphiLexer lexer = new DelphiLexer(charStream);
            
            // Create token stream
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            // Create parser
            DelphiParser parser = new DelphiParser(tokens);
            
            // Parse the input
            ParseTree tree = parser.program();
            
            // Create and run interpreter
            DelphiInterpreter interpreter = new DelphiInterpreter();
            interpreter.visit(tree);
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error during interpretation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
