import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Initialize the lexer
        LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(args[0]));
        Symbol token;
        ArrayList<Symbol> symbols = new ArrayList<>();

        // Read all tokens
        while ((token = lexer.next_token()).getType() != LexicalUnit.EOS) {
            System.out.println(token.toString());
        }
    }
}