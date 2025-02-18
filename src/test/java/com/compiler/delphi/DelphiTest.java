package com.compiler.delphi;

import org.junit.Test;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class DelphiTest {
    @Test
    public void testBasicClassDeclaration() {
        String input = "PROGRAM Test;\n" +
                      "CLASS Animal\n" +
                      "PUBLIC\n" +
                      "  constructor Create;\n" +
                      "  name: STRING;\n" +
                      "END;\n" +
                      "\n" +
                      "constructor Animal.Create;\n" +
                      "BEGIN\n" +
                      "  name := 'Generic Animal';\n" +
                      "END;\n" +
                      "\n" +
                      "BEGIN\n" +
                      "END.";

        DelphiLexer lexer = new DelphiLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);
        ParseTree tree = parser.program();

        DelphiInterpreter interpreter = new DelphiInterpreter();
        interpreter.visit(tree);
    }
}