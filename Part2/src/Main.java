import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1 || args.length > 3) {
                System.err.println("Usage: java Main [-wt <output file>] <input file>");
                return;
            }

            String inputFile;
            String outputFile = null;

            // Check if we have the optional -wt argument
            if (args.length == 3) {
                if (!args[0].equals("-wt")) {
                    System.err.println("Usage: java Main [-wt <output file>] <input file>");
                    return;
                }
                outputFile = args[1];
                inputFile = args[2];
            } else if (args.length == 1) {
                inputFile = args[0];
            } else {
                System.err.println("Usage: java Main [-wt <output file>] <input file>");
                return;
            }

            // Create the lexical analyzer and a parser
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(inputFile));
            Parser parser = new Parser(lexer);

            // Parse the program and generate the parse tree
            parser.parseProgram();

            if (outputFile != null) {
                parser.parseTree.writeParseTreeToLaTeX(outputFile);
            } else {
                // Print the derivation if no LaTex output is requested
                parser.printDerivation();
            }
        }
        catch (IOException e) {
            System.err.println("An I/O error occurred while reading the input: " + e.getMessage());
        }
        catch (UnexpectedTokenException e) {
            System.err.println("UnexpectedTokenException: " + e.getMessage());
        }
    }
}
