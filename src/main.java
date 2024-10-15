import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the lexer
            LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(args[0]));
            Symbol token;
            HashMap<String, Integer> variables = new HashMap<String, Integer>();
            // Read all tokens
            while ((token = lexer.yylex()).getType() != LexicalUnit.EOS) {
                System.out.println(token.toString());
                if(token.getType() == LexicalUnit.VARNAME && !variables.containsKey(token.getValue())) {
                    variables.put(token.getValue().toString(), token.getLine());
                }
            }
            System.out.println("\nVariables");
            for (String key : variables.keySet()) {
                System.out.println(key + "\t" + variables.get(key));
            }
        }
        catch (IOException e) {
            System.err.println("An I/O error occured while reading the input: " + e.getMessage());
        } 
    }
}