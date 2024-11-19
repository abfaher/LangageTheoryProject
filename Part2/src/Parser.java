import java.io.IOException;
import java.util.*;

public class Parser {

    private final LexicalAnalyzer scanner;
    private final List<Integer> derivation;
    private Symbol currentToken;
    
    public Parser(LexicalAnalyzer scanner) {
        this.scanner = scanner;
        this.derivation = new ArrayList<>();
    }

    private void advance() {
        try {
            currentToken = scanner.nextToken();
        } catch (IOException e) {
            //error("Error reading input.");
        }
    }

    private void match(LexicalUnit expected) {
        if (currentToken.getType() == expected) {
            advance();
        } else {
            //error("Expected " + expected + " but found " + currentToken.getType());
        }
    }

    public void parseProgram() {
        derivation.add(1); // Rule 1: <Program> → LET [ProgName] BE <Code> END
        match(LexicalUnit.LET);
        match(LexicalUnit.PROGNAME);
        match(LexicalUnit.BE);
        parseCode();
        match(LexicalUnit.END);
    }

    private void parseCodePrime() {
        if (currentToken.getType() == LexicalUnit.COLUMN) {
            match(LexicalUnit.COLUMN);
            parseCode();
        } else { // TODO: REGLE 3 A RAJOUTER
            //error("Unexpected token in <CodePrime>");
        }
    }

    private void parseCode() {
        derivation.add(2); // Rule 2: <Code> → <Instruction> : <Code>
        parseInstruction();
        match(LexicalUnit.COLUMN);
        parseCodePrime();
    }

    private void parseInstruction() {
       if (currentToken.getType() == LexicalUnit.ASSIGN) {
            derivation.add(4); // Rule 4: <Instruction> → <Assignment>
            parseAssignment();
        } else if (currentToken.getType() == LexicalUnit.IF) {
            derivation.add(5); // Rule 5: <Instruction> → <If>
            parseIf();
        } else if (currentToken.getType() == LexicalUnit.WHILE) {
            derivation.add(6); // Rule 6: <Instruction> → <While>
            parseWhile();
        }
        // TODO: Rule 7 is missing <Call> is not defined in the LexicalUnit class !!
        else if (currentToken.getType() == LexicalUnit.OUTPUT) {
            derivation.add(8); // Rule 8: <Instruction> → <Out>
            parseOut();
        } else if (currentToken.getType() == LexicalUnit.INPUT) {
            derivation.add(9); // Rule 9: <Instruction> → <In>
            parseIn();
        } else {
            //error("Unexpected token in <Instruction>");
        }
    }

    private void parseAssignment() {
        derivation.add(10); // Rule 10: <Assignment> → [VarName] = <ExprArith>
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        parseExprArith();
    }

    // à revoir pour les règles 11 et 12
    private void parseExprArith() {
        parseTerm();
        boolean opExist = false;

        while (currentToken.getType() == LexicalUnit.PLUS || currentToken.getType() == LexicalUnit.MINUS) {
            if (currentToken.getType() == LexicalUnit.PLUS) {
                derivation.add(11);
                match(LexicalUnit.PLUS);
                opExist = true;
            } else if ((currentToken.getType() == LexicalUnit.MINUS)) {
                derivation.add(12);
                match(LexicalUnit.MINUS);
                opExist = true;
            }
            parseTerm();
        }
        if (!opExist) {
            derivation.add(13); // Rule 13: <ExprArith> -> <Term>
        }
    }

    private void parseTerm() {
        parseFactor(); // Rule 16 is explicitly handled by this line
        boolean opExist = false;

        while (currentToken.getType() == LexicalUnit.TIMES || currentToken.getType() == LexicalUnit.DIVIDE) {
            if (currentToken.getType() == LexicalUnit.TIMES) {
                derivation.add(14); // Rule 14: <Term> → <Factor> * <Term>
                match(LexicalUnit.TIMES);
                opExist = true;
            } else if (currentToken.getType() == LexicalUnit.DIVIDE) {
                derivation.add(15); // Rule 15: <Term> → <Factor> / <Term>   ICI ON FAIT FACTOR / TERM et NON PAS TERM / FACTOR !!!!!
                match(LexicalUnit.DIVIDE);
                opExist = true;
            }
            parseFactor();
        }
        if (!opExist) {
            derivation.add(16); // Rule 16: <Term> → <Factor>
        }
    }

    private void parseFactor() {
        if (currentToken.getType() == LexicalUnit.VARNAME){
            derivation.add(17); // Rule 17: <Factor> → [VarName]
            match(LexicalUnit.VARNAME);
        } else if (currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(18); // Rule 18: <Factor> → [Number]
            match(LexicalUnit.NUMBER);
        } else if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(19); // Rule 19: <Factor> → - <Factor>
            match(LexicalUnit.MINUS);
            parseFactor();
        } else if (currentToken.getType() == LexicalUnit.LPAREN) {
            derivation.add(20); // Rule 20: <Factor> → ( <ExprArith> )
            match(LexicalUnit.LPAREN);
            parseExprArith();
            match(LexicalUnit.RPAREN);
        } else {
            //error("Unexpected token in <Factor>");
        }
    }

    private void parseIf() {
        match(LexicalUnit.IF);
        match(LexicalUnit.LBRACK);
        parseCond();
        match(LexicalUnit.RBRACK);
        match(LexicalUnit.THEN);
        parseCode();
        if (currentToken.getType() == LexicalUnit.ELSE) {
            match(LexicalUnit.ELSE);
            derivation.add(22); // Rule 22: <If> → IF [Cond] THEN <Code> ELSE <Code>
            parseCode();
        } else {
            derivation.add(21); // Rule 21: <If> → IF [Cond] THEN <Code>
        }
        match(LexicalUnit.END);
    }

    private void parseCond() {
        parseCondImpl(); // Start with the highest precedence level
        derivation.add(23); // Rule 23: <Cond> → <CondImpl>
    }

    private void parseCondImpl() {
        parseCondOr(); // Parse lower precedence <CondOr>
    
        while (currentToken.getType() == LexicalUnit.IMPLIES) {
            derivation.add(24); // Rule 24: <Cond> → <Cond> -> <Cond>
            match(LexicalUnit.IMPLIES);
            parseCondOr();
        }
    }

    private void parseCondOr() {
        parseCondBase(); // Parse the base condition
    
        while (currentToken.getType() == LexicalUnit.PIPE) {
            derivation.add(25); // Rule 25: <Cond> → <Cond> | <Cond>
            match(LexicalUnit.PIPE);
            parseCondBase();
        }
    }
    
    private void parseCondBase() {
        parseExprArith();
        derivation.add(26); // Rule 26: <Cond> → <ExprArith> <Comp> <ExprArith>
        parseComp();
        parseExprArith();
    }

    private void parseComp() {
        if (currentToken.getType() == LexicalUnit.EQUAL) {
            derivation.add(27); // Rule 27: <Comp> → ==
            match(LexicalUnit.EQUAL);
        } else if (currentToken.getType() == LexicalUnit.SMALEQ) {
            derivation.add(28); // Rule 28: <Comp> → <=
            match(LexicalUnit.SMALEQ);
        } else if (currentToken.getType() == LexicalUnit.SMALLER) {
            derivation.add(29); // Rule 29: <Comp> → <
            match(LexicalUnit.SMALLER);
        } else {
            //error("Unexpected token in <Comp>");
        }
    }

    private void parseWhile() {
        derivation.add(30); // Rule 30: <While> → WHILE [Cond] REPEAT <Code> END
        match(LexicalUnit.WHILE);
        match(LexicalUnit.LBRACK);
        parseCond();
        match(LexicalUnit.RBRACK);
        match(LexicalUnit.REPEAT);
        parseCode();
        match(LexicalUnit.END);
    }

    private void parseOut() {
        derivation.add(31); // Rule 31: <Out> → OUT [VarName]
        match(LexicalUnit.OUTPUT);
        match(LexicalUnit.VARNAME);
    }

    private void parseIn() {
        derivation.add(33); // Rule 33: <In> → IN [VarName]
        match(LexicalUnit.INPUT);
        match(LexicalUnit.VARNAME);
    }

    // derivation of LL(1) parsing table

    // derivation of the parse tree

}
