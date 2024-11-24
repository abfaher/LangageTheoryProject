import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println("Usage: java Main <input file> [-wt <output file>]");
                return;
            }

            // Create the lexical analyzer and a parser
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(args[0]));
            Parser parser = new Parser(lexer);

            // Parse the program and generate the parse tree
            parser.parseProgram();

            if (args.length == 3 && args[1].equals("-wt")) {
                String outputFile = args[2];
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
