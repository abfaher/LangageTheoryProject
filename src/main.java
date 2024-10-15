import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the lexer
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(args[0]));
            Symbol token;
            ArrayList<Symbol> symbols = new ArrayList<>();
            // Read all tokens
            while ((token = lexer.yylex()).getType() != LexicalUnit.EOS) {
                System.out.println(token.toString());
            }
        }
        catch (IOException e) {
            System.err.println("An I/O error occured while reading the input: " + e.getMessage());
        } 
    }
}