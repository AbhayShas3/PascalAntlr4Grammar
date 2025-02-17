package com.compiler.delphi;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class DelphiTest {

    private ParseTree parse(String input) {
        DelphiLexer lexer = new DelphiLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);
        return parser.program();
    }

    @Test
    public void testSimpleProgram() {
        String input = "PROGRAM Test;\n" +
                      "VAR\n" +
                      "    x: integer;\n" +
                      "BEGIN\n" +
                      "    x := 42\n" +
                      "END.\n";
        
        ParseTree tree = parse(input);
        assertNotNull("Parse tree should not be null", tree);
    }

    @Test
    public void testSimpleClass() {
        String input = 
            "PROGRAM TestClass;\n" +
            "\n" +
            "class Person\n" +
            "private\n" +
            "    age: integer;\n" +
            "    name: string;\n" +
            "public\n" +
            "    constructor Create(newName: string; newAge: integer);\n" +
            "    destructor Destroy;\n" +
            "    procedure PrintInfo;\n" +
            "end;\n" +
            "\n" +
            "constructor Person.Create(newName: string; newAge: integer);\n" +
            "begin\n" +
            "    name := newName;\n" +
            "    age := newAge;\n" +
            "end;\n" +
            "\n" +
            "destructor Person.Destroy;\n" +
            "begin\n" +
            "end;\n" +
            "\n" +
            "procedure Person.PrintInfo;\n" +
            "begin\n" +
            "    writeln('Name: ', name, ', Age: ', age);\n" +
            "end;\n" +
            "\n" +
            "VAR\n" +
            "    person: Person;\n" +
            "BEGIN\n" +
            "    person := Person.Create('John', 30);\n" +
            "    person.PrintInfo;\n" +
            "    person.Destroy;\n" +
            "END.\n";

        ParseTree tree = parse(input);
        assertNotNull("Parse tree should not be null", tree);
    }

    @Test
    public void testInheritance() {
        String input = 
            "PROGRAM TestInheritance;\n" +
            "\n" +
            "class Animal\n" +
            "protected\n" +
            "    name: string;\n" +
            "public\n" +
            "    constructor Create(animalName: string);\n" +
            "    procedure MakeSound; virtual;\n" +
            "end;\n" +
            "\n" +
            "class Dog inherits Animal\n" +
            "public\n" +
            "    procedure MakeSound; override;\n" +
            "end;\n" +
            "\n" +
            "constructor Animal.Create(animalName: string);\n" +
            "begin\n" +
            "    name := animalName;\n" +
            "end;\n" +
            "\n" +
            "procedure Animal.MakeSound;\n" +
            "begin\n" +
            "    writeln('Generic animal sound');\n" +
            "end;\n" +
            "\n" +
            "procedure Dog.MakeSound;\n" +
            "begin\n" +
            "    writeln('Woof! Woof!');\n" +
            "end;\n" +
            "\n" +
            "VAR\n" +
            "    myDog: Dog;\n" +
            "BEGIN\n" +
            "    myDog := Dog.Create('Buddy');\n" +
            "    myDog.MakeSound;\n" +
            "END.\n";

        ParseTree tree = parse(input);
        assertNotNull("Parse tree should not be null", tree);
    }

    @Test
    public void testComplexFeatures() {
        String input = 
            "PROGRAM TestComplex;\n" +
            "\n" +
            "type\n" +
            "    TDayOfWeek = (Monday, Tuesday, Wednesday, Thursday, Friday);\n" +
            "    TIntegerArray = array[1..10] of integer;\n" +
            "\n" +
            "class Calculator\n" +
            "private\n" +
            "    values: TIntegerArray;\n" +
            "    count: integer;\n" +
            "public\n" +
            "    constructor Create;\n" +
            "    procedure AddValue(value: integer);\n" +
            "    function GetAverage: real;\n" +
            "end;\n" +
            "\n" +
            "constructor Calculator.Create;\n" +
            "begin\n" +
            "    count := 0;\n" +
            "end;\n" +
            "\n" +
            "procedure Calculator.AddValue(value: integer);\n" +
            "begin\n" +
            "    if count < 10 then\n" +
            "    begin\n" +
            "        count := count + 1;\n" +
            "        values[count] := value;\n" +
            "    end;\n" +
            "end;\n" +
            "\n" +
            "function Calculator.GetAverage: real;\n" +
            "var\n" +
            "    sum: integer;\n" +
            "    i: integer;\n" +
            "begin\n" +
            "    sum := 0;\n" +
            "    for i := 1 to count do\n" +
            "        sum := sum + values[i];\n" +
            "    if count > 0 then\n" +
            "        GetAverage := sum / count\n" +
            "    else\n" +
            "        GetAverage := 0;\n" +
            "end;\n" +
            "\n" +
            "VAR\n" +
            "    calc: Calculator;\n" +
            "    day: TDayOfWeek;\n" +
            "BEGIN\n" +
            "    calc := Calculator.Create;\n" +
            "    calc.AddValue(10);\n" +
            "    calc.AddValue(20);\n" +
            "    writeln('Average: ', calc.GetAverage);\n" +
            "    day := Monday;\n" +
            "END.\n";

        ParseTree tree = parse(input);
        assertNotNull("Parse tree should not be null", tree);
    }
}