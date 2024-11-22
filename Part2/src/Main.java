import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(args[0]));
            Parser parser = new Parser(lexer);
            // Symbol token;
            parser.parseProgram();
            // while ((token = lexer.nextToken()).getType() != LexicalUnit.EOS) {
            parser.printDerivation();
        }
        catch (IOException e) {
            System.err.println("An I/O error occured while reading the input: " + e.getMessage());
        } 




    }
}
