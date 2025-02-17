package com.compiler.parser;  // Changed package to match generated files

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.BaseErrorListener;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PascalParserTest {
    
    @Test
    public void testClassDeclaration() throws IOException {
        String testProgram = new String(Files.readAllBytes(
            Paths.get("src/test/resources/Test.pas")));
        
        // Create a CharStream from the test program
        CharStream input = CharStreams.fromString(testProgram);
        
        // Create lexer
        PascalLexer lexer = new PascalLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                  int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("Lexer error at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });
        
        // Create token stream
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Create parser
        PascalParser parser = new PascalParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                  int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("Parser error at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });
        
        // Parse the input
        ParseTree tree = parser.program();
        
        // Print the parse tree for debugging
        System.out.println("Parse tree:");
        System.out.println(tree.toStringTree(parser));
        
        // Assert no syntax errors
        assertEquals("There should be no syntax errors", 0, parser.getNumberOfSyntaxErrors());
    }
}