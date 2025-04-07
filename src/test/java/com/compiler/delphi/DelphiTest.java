package com.compiler.delphi;

// Added imports for ANTLR generated classes and the interpreter
import com.compiler.delphi.DelphiLexer;
import com.compiler.delphi.DelphiParser;
import com.compiler.delphi.DelphiInterpreter;

// Existing ANTLR runtime and JUnit imports
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
// Added imports for the ANTLR Error Listener components
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

// JUnit imports
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals; // Keep if needed for future assertions
import static org.junit.Assert.assertNotNull;

/**
 * JUnit tests for the Delphi Interpreter.
 */
public class DelphiTest {

    /**
     * Helper method to create lexer, parser, and interpreter for a given test file.
     * @param filename The name of the Pascal/Delphi file in src/test/resources/testcases/
     * @return The result of visiting the parse tree (may be null or specific object depending on interpreter logic)
     * @throws IOException If the test file cannot be read.
     */
    private Object interpretFile(String filename) throws IOException {
        String fullPath = "testcases/" + filename;
        // Use ClassLoader to reliably find resources within the test classpath
        InputStream is = getClass().getClassLoader().getResourceAsStream(fullPath);
        if (is == null) {
            // Provide a more informative error message if the resource is not found
            throw new IOException("Could not find resource file: '" + fullPath + "'. Ensure it's in src/test/resources/testcases/");
        }

        // Create ANTLR stream, lexer, tokens, parser
        CharStream input = CharStreams.fromStream(is);
        DelphiLexer lexer = new DelphiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);

        // Add an error listener for better diagnostics during parsing
        parser.removeErrorListeners(); // Remove default console error listener
        parser.addErrorListener(new BaseErrorListener() { // Now finds the symbol
             @Override // This should now be recognized correctly
             public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) { // Now finds Recognizer and RecognitionException
                  // Throw an exception to fail the test immediately on syntax error
                  throw new IllegalStateException("Failed to parse at line " + line + ":" + charPositionInLine + " due to " + msg, e);
             }
        });


        // Parse the program rule
        ParseTree tree = parser.program();

        // Create and run the interpreter visitor
        DelphiInterpreter interpreter = new DelphiInterpreter();
        return interpreter.visit(tree);
    }

    // --- Test Cases ---

    @Test
    public void testBasicArithmetic() throws IOException {
        System.out.println("\n--- Testing basicArithmetic.pas ---");
        Object result = interpretFile("basicArithmetic.pas");
        // Add more specific assertions based on expected output or final variable states if needed
        assertNotNull("Interpreter should run without throwing an exception", result); // Basic check
        System.out.println("--- Finished basicArithmetic.pas ---");
    }

    @Test
    public void testProcedureCall() throws IOException {
        System.out.println("\n--- Testing procedureCall.pas ---");
        Object result = interpretFile("procedureCall.pas");
        assertNotNull("Interpreter should run without throwing an exception", result);
        System.out.println("--- Finished procedureCall.pas ---");
    }

    @Test
    public void testIfStatement() throws IOException {
        System.out.println("\n--- Testing ifStatement.pas ---");
        Object result = interpretFile("ifStatement.pas");
        assertNotNull("Interpreter should run without throwing an exception", result);
        System.out.println("--- Finished ifStatement.pas ---");
    }

    @Test
    public void testWhileLoop() throws IOException {
        System.out.println("\n--- Testing whileLoop.pas ---");
        Object result = interpretFile("whileLoop.pas");
        assertNotNull("Interpreter should run without throwing an exception", result);
        System.out.println("--- Finished whileLoop.pas ---");
    }

    @Test
    public void testForLoop() throws IOException {
        System.out.println("\n--- Testing forLoop.pas ---");
        Object result = interpretFile("forLoop.pas");
        assertNotNull("Interpreter should run without throwing an exception", result);
        System.out.println("--- Finished forLoop.pas ---");
    }
}
