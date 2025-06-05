package org.example;

import org.example.compiler.Compiler;
import org.example.errors.ErrorHandler; // Assuming you have this

public class App {
    public static void main(String[] args) {
        if (args.length < 2 || !args[0].equals("run")) {
            System.err.println("Usage: run <filename>");
            System.exit(1);
        }

        String command = args[0];
        String oakFileName = args[1];

        Compiler compiler = new Compiler(oakFileName, command);

        // Check if the compiler is in a state to proceed
        if (compiler.canCompile()) {
            compiler.compile();
            // Potentially add more steps here: parser, AST, code generation, etc.
        } else {
            System.err.println("Compilation failed due to file reading issues. See errors above.");
            System.exit(1);
        }
    }
}