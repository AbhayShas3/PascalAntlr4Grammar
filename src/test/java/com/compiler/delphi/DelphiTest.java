package com.compiler.delphi;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DelphiTest {
    
    private ParseTree parse(String input) {
        CharStream charStream = CharStreams.fromString(input);
        DelphiLexer lexer = new DelphiLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);
        return parser.program();
    }
    
    @Test
    public void testBasicClassDeclaration() {
        String program = """
            program TestProgram;
            class Person
                private
                    name: string;
                    age: integer;
                public
                    constructor create(n: string; a: integer);
                    function getName: string;
            end;
            begin
            end.
            """;
            
        ParseTree tree = parse(program);
        DelphiInterpreter interpreter = new DelphiInterpreter();
        interpreter.visit(tree);
    }
}