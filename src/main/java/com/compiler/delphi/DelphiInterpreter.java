package com.compiler.delphi;

import java.util.*;

public class DelphiInterpreter extends DelphiBaseVisitor<Object> {
    private final Map<String, ClassDef> classes = new HashMap<>();
    private final Map<String, ObjectInstance> objects = new HashMap<>();
    private final Map<String, Object> symbolTable = new HashMap<>();
    private ClassDef currentClass;
    private String currentVisibility = "private";

    @Override
    public Object visitProgram(DelphiParser.ProgramContext ctx) {
        visit(ctx.block());
        classes.values().forEach(System.out::println);
        return null;
    }

    @Override
    public Object visitBlock(DelphiParser.BlockContext ctx) {
        for (DelphiParser.ClassDeclarationPartContext classCtx : ctx.classDeclarationPart()) {
            visit(classCtx);
        }
        for (DelphiParser.ConstructorImplementationContext constructorCtx : ctx.constructorImplementation()) {
            visit(constructorCtx);
        }
        if (ctx.compoundStatement() != null) {
            return visit(ctx.compoundStatement());
        }
        return null;
    }

    @Override
    public Object visitClassDeclarationPart(DelphiParser.ClassDeclarationPartContext ctx) {
        String className = ctx.className().getText();
        currentClass = new ClassDef(className);
        
        if (ctx.INHERITS() != null && ctx.baseClassName() != null) {
            String parentClass = ctx.baseClassName().getText();
            if (!classes.containsKey(parentClass)) {
                throw new RuntimeException("Parent class " + parentClass + " not found");
            }
            currentClass.setParentClass(parentClass);
        }
        
        classes.put(className, currentClass);
        visit(ctx.visibilityBlock());
        return null;
    }

    @Override
    public Object visitVisibilitySection(DelphiParser.VisibilitySectionContext ctx) {
        currentVisibility = ctx.visibility().getText().toLowerCase();
        return visitChildren(ctx);
    }

    @Override
    public Object visitClassVarDeclaration(DelphiParser.ClassVarDeclarationContext ctx) {
        String name = ctx.identifier().getText();
        String type = ctx.type_().getText();
        currentClass.addField(name, type, currentVisibility);
        return null;
    }

    @Override
    public Object visitMethodDeclaration(DelphiParser.MethodDeclarationContext ctx) {
        if (ctx.constructorDeclaration() != null) {
            String name = ctx.constructorDeclaration().identifier().getText();
            currentClass.addConstructor(name, currentVisibility);
            
            if (ctx.constructorDeclaration().formalParameterList() != null) {
                visit(ctx.constructorDeclaration().formalParameterList());
            }
        }
        return null;
    }
    

    @Override
    public Object visitConstructorImplementation(DelphiParser.ConstructorImplementationContext ctx) {
        if (ctx == null || ctx.className() == null) {
            return null;
        }
        
        String className = ctx.className().getText();
        if (ctx.identifier() != null) {
            String methodName = ctx.identifier().getText();
            ClassDef classDef = classes.get(className);
            
            if (classDef != null && ctx.block() != null) {
                currentClass = classDef;
                DelphiParser.CompoundStatementContext compound = ctx.block().compoundStatement();
                if (compound != null && compound.statements() != null) {
                    for (DelphiParser.StatementContext stmt : compound.statements().statement()) {
                        visit(stmt);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object visitFormalParameterList(DelphiParser.FormalParameterListContext ctx) {
        if (currentClass != null && ctx.formalParameterSection() != null) {
            for (DelphiParser.FormalParameterSectionContext section : ctx.formalParameterSection()) {
                DelphiParser.ParameterGroupContext group = section.parameterGroup();
                if (group != null) {
                    String type = group.typeIdentifier().getText();
                    for (DelphiParser.IdentifierContext id : group.identifierList().identifier()) {
                        currentClass.constructor.addParameter(id.getText(), type);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx) {
        String varName = ctx.variable().getText();
        Object value = visit(ctx.expression());
        
        if (currentClass != null) {
            currentClass.setFieldValue(varName, value.toString());
        } else {
            symbolTable.put(varName, value);
        }
        return null;
    }

    @Override
    public Object visitProcedureStatement(DelphiParser.ProcedureStatementContext ctx) {
        String identifier = ctx.identifier().getText();
        List<Object> args = new ArrayList<>();
        
        if (ctx.parameterList() != null) {
            for (DelphiParser.ActualParameterContext param : ctx.parameterList().actualParameter()) {
                args.add(visit(param));
            }
        }
        
        if (identifier.toLowerCase().startsWith("new")) {
            String className = identifier.substring(3);
            ClassDef classDef = classes.get(className);
            if (classDef != null) {
                ObjectInstance instance = new ObjectInstance(classDef);
                
                // Set constructor parameters
                if (!args.isEmpty() && classDef.constructor != null && 
                    classDef.constructor.parameters.size() == args.size()) {
                    for (int i = 0; i < args.size(); i++) {
                        String paramName = classDef.constructor.parameters.get(i).name;
                        instance.setField(paramName, args.get(i));
                    }
                }
                
                symbolTable.put("RESULT", instance);
            }
        }
        return null;
    }

    @Override
    public Object visitExpression(DelphiParser.ExpressionContext ctx) {
        if (ctx.simpleExpression() != null) {
            return visit(ctx.simpleExpression());
        }
        return null;
    }

    @Override
    public Object visitSimpleExpression(DelphiParser.SimpleExpressionContext ctx) {
        if (ctx.term() != null) {
            return visit(ctx.term());
        }
        return null;
    }

    @Override
    public Object visitTerm(DelphiParser.TermContext ctx) {
        if (ctx.signedFactor() != null) {
            return visit(ctx.signedFactor());
        }
        return null;
    }

    @Override
    public Object visitSignedFactor(DelphiParser.SignedFactorContext ctx) {
        return visit(ctx.factor());
    }

    @Override
    public Object visitFactor(DelphiParser.FactorContext ctx) {
        if (ctx.unsignedConstant() != null) {
            return visit(ctx.unsignedConstant());
        }
        if (ctx.variable() != null) {
            return visit(ctx.variable());
        }
        return null;
    }

    @Override
    public Object visitUnsignedConstant(DelphiParser.UnsignedConstantContext ctx) {
        if (ctx.string() != null) {
            return visit(ctx.string());
        }
        if (ctx.unsignedNumber() != null) {
            return visit(ctx.unsignedNumber());
        }
        return null;
    }

    @Override
    public Object visitString(DelphiParser.StringContext ctx) {
        return ctx.STRING_LITERAL().getText().replace("'", "");
    }

    private class ClassDef {
        private final String name;
        private String parentClass;
        private final Map<String, Field> fields = new HashMap<>();
        private Constructor constructor;
        private final List<Method> methods = new ArrayList<>();

        public ClassDef(String name) {
            this.name = name;
        }

        public void setParentClass(String parent) {
            this.parentClass = parent;
        }

        public void addField(String name, String type, String visibility) {
            fields.put(name, new Field(name, type, visibility));
        }

        public void addConstructor(String name, String visibility) {
            constructor = new Constructor(name, visibility);
        }

        public void addMethod(String name, String returnType, String visibility) {
            methods.add(new Method(name, returnType, visibility));
        }

        public void setFieldValue(String name, String value) {
            if (fields.containsKey(name)) {
                fields.get(name).value = value;
            } else if (parentClass != null) {
                ClassDef parent = classes.get(parentClass);
                if (parent != null) {
                    Field parentField = parent.fields.get(name);
                    if (parentField != null && !parentField.visibility.equals("private")) {
                        parentField.value = value;
                    }
                }
            }
        }

        public Map<String, Field> getAllFields() {
            Map<String, Field> allFields = new HashMap<>(fields);
            if (parentClass != null) {
                ClassDef parent = classes.get(parentClass);
                if (parent != null) {
                    parent.getAllFields().forEach((name, field) -> {
                        if (!field.visibility.equals("private")) {
                            allFields.put(name, field);
                        }
                    });
                }
            }
            return allFields;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Class ").append(name);
            if (parentClass != null) {
                sb.append(" inherits ").append(parentClass);
            }
            sb.append(":\n");
            
            getAllFields().forEach((fieldName, field) -> 
                sb.append("  ").append(field.visibility)
                  .append(" ").append(field.type)
                  .append(" ").append(fieldName)
                  .append(field.value != null ? " = " + field.value : "")
                  .append("\n"));
            
            if (constructor != null) {
                sb.append("  ").append(constructor.visibility)
                  .append(" constructor ").append(constructor.name);
                if (!constructor.parameters.isEmpty()) {
                    sb.append("(");
                    constructor.parameters.forEach(param -> 
                        sb.append(param.name).append(": ").append(param.type).append(", "));
                    sb.setLength(sb.length() - 2);
                    sb.append(")");
                }
                sb.append("\n");
            }
            
            return sb.toString();
        }
    }

    private static class Field {
        private final String name;
        private final String type;
        private final String visibility;
        private String value;

        public Field(String name, String type, String visibility) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
        }
    }

    private static class Parameter {
        private final String name;
        private final String type;
        private Object value;

        public Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    private static class Constructor {
        private final String name;
        private final String visibility;
        private final List<Parameter> parameters;

        public Constructor(String name, String visibility) {
            this.name = name;
            this.visibility = visibility;
            this.parameters = new ArrayList<>();
        }

        public void addParameter(String name, String type) {
            parameters.add(new Parameter(name, type));
        }
    }

    private static class Method {
        private final String name;
        private final String returnType;
        private final String visibility;
        private final List<Parameter> parameters;

        public Method(String name, String returnType, String visibility) {
            this.name = name;
            this.returnType = returnType;
            this.visibility = visibility;
            this.parameters = new ArrayList<>();
        }

        public void addParameter(String name, String type) {
            parameters.add(new Parameter(name, type));
        }
    }

    public class ObjectInstance {
        private final ClassDef classDef;
        private final Map<String, Object> fields;
        
        public ObjectInstance(ClassDef classDef) {
            this.classDef = classDef;
            this.fields = new HashMap<>();
            classDef.getAllFields().forEach((name, field) -> 
                fields.put(name, getDefaultValue(field.type)));
        }

        private Object getDefaultValue(String type) {
            switch(type.toUpperCase()) {
                case "INTEGER": return 0;
                case "REAL": return 0.0;
                case "STRING": return "";
                case "BOOLEAN": return false;
                default: return null;
            }
        }

        public void setField(String name, Object value) {
            Field field = classDef.getAllFields().get(name);
            if (field != null && !field.visibility.equals("private")) {
                fields.put(name, value);
            } else {
                throw new RuntimeException("Cannot access private field: " + name);
            }
        }

        public Object getField(String name) {
            Field field = classDef.getAllFields().get(name);
            if (field != null && !field.visibility.equals("private")) {
                return fields.get(name);
            }
            throw new RuntimeException("Cannot access private field: " + name);
        }

        @Override
        public String toString() {
            return "Instance of " + classDef.name + ": " + fields;
        }
    }
}