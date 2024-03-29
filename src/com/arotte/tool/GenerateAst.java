package com.arotte.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** GenerateAst.java
 *
 * This class generates the Abstract Syntax Tree
 * for the Aroi programming language.
 *
 * TODO: possibly rewrite this in a scripting language
 */
public class GenerateAst {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        // generate AST class for expressions
        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign   : Token name, Expr value",
                "Literal  : Object value",
                "Logical  : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Unary    : Token operator, Expr right",
                "Binary   : Expr left, Token operator, Expr right",
                "Variable : Token name"
        ));

        // generate AST class for statements
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> grammar) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package com.arotte.aroi;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println(javadoc(baseName));
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, grammar);

        // the AST classes
        System.out.println("Generating inner classes.");
        for (String type : grammar) {
            String className = type.split(":")[0].trim();
            String fields    = type.split(":")[1].trim();
            defineType(writer, className, fields, baseName);
            System.out.println(tab(1) + "Class '" + className + "' generated.");
        }

        // base accept() method for the visitor pattern
        writer.println(tab(1) + "abstract <R> R accept(Visitor<R> visitor);");

        // end of class definition
        writer.println("}");
        writer.close();

        System.out.println("Done.");
        System.out.println("Saved to '" + path + "'.");
    }

    /**
     * Define the visitor interface inside the base class.
     * @param writer PrintWriter that prints the strings
     */
    private static void defineVisitor(PrintWriter writer, String base, List<String> grammar) {
        writer.println(tab(1) + "interface Visitor<R> {");

        for (String type : grammar) {
            String typeName = type.split(":")[0].trim();
            writer.println(tab(2) +
                    "R visit" + typeName + base + "(" + typeName + " " + base.toLowerCase() + ");"
            );
        }

        writer.println(tab(1) + "}");
        writer.println();
        System.out.println("Visitor interface generated.");
    }

    private static void defineType(PrintWriter writer, String className, String fieldList, String base) {

        writer.println(tab(1) +
                "static class " + className + " extends " + base + " {");

        // fields of the class
        writer.println(tab(2) + "// fields");
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            writer.println(tab(2) + "final " + field + ";");
        }

        // constructor
        writer.println(tab(2) + "// constructor");
        writer.println(tab(2) +
                className + "(" + fieldList + ") {");

        // set fields to parameters
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(tab(3) +
                    "this." + name + " = " + name + ";");
        }

        // end of constructor
        writer.println(tab(2) + "}");

        // the Visitor pattern: override accept() function
        writer.println(tab(2) + "// visitor pattern");
        writer.println(tab(2) + "@Override");
        writer.println(tab(2) + "<R> R accept(Visitor<R> visitor) {");
        writer.println(tab(3) + "return visitor.visit" + className + base + "(this);");
        writer.println(tab(2) + "}");

        // end of class definition
        writer.println(tab(1) + "}");
        writer.println();
    }

    private static String javadoc(String base) {
        return "/**" + base + ".java\n" +
                " *\n" +
                " * THIS IS A GENERATED FILE.\n" +
                " * DO NOT MODIFY!\n" +
                " *\n" +
                " * Generated by com.arotte.tool.GenerateAst.java\n" +
                " */";
    }

    private static String tab(int depth) {
        StringBuilder t = new StringBuilder();
        if (depth > 0) { // && depth < 100
            t.append("  ".repeat(depth));
        }
        return t.toString();
    }
}
