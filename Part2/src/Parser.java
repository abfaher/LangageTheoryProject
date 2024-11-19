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

    public void parseCode() {
        if (currentToken.getType() == LexicalUnit.ASSIGN || currentToken.getType() == LexicalUnit.IF || currentToken.getType() == LexicalUnit.WHILE || currentToken.getType() == LexicalUnit.OUTPUT || currentToken.getType() == LexicalUnit.INPUT) {
            derivation.add(2); // Rule 2: <Code> → <Instruction> : <Code>
            parseInstruction();
            match(LexicalUnit.COLUMN);
            parseCode();
        } else if (currentToken.getType() == LexicalUnit.END) {
            derivation.add(3); // Rule 3: <Code> → ε
        } else {
            //error("Unexpected token in <Code>");
        }
    }

    public void parseInstruction() {
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
        // Rule 7 is missing <Call> is not defined in the LexicalUnit class !!
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

    public void parseAssignment() {
        derivation.add(10); // Rule 10: <Assignment> → [VarName] = <ExprArith>
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        parseExprArith();
    }

    // à revoir pour les règles 11 et 12
    public void parseExprArith() {
        parseTerm();
        derivation.add(13); // Rule 13: <ExprArith> -> <Term>

        if (currentToken.getType() == LexicalUnit.PLUS) {
            derivation.remove(derivation.size() - 1); // pop the 13 rule from the derivation
            derivation.add(11);
            parseOp();
            parseTerm();
        } else if ((currentToken.getType() == LexicalUnit.MINUS)) {
            derivation.remove(derivation.size() - 1); // pop the 13 rule from the derivation
            derivation.add(12);
            parseOp();
            parseTerm();
        } else {
            //error("Unexpected token in <ExprArith>");
        }
    }

    public void parseTerm() {
        parseFactor(); // Rule 16 is explicitly handled by this line
        derivation.add(16); // Rule 16: <Term> → <Factor>
        if (currentToken.getType() == LexicalUnit.TIMES) {
            derivation.remove(derivation.size() - 1); // pop the 16 rule from the derivation
            derivation.add(14); // Rule 14: <Term> → <Factor> * <Term>
            parseOp();
            parseTerm();
        } else if (currentToken.getType() == LexicalUnit.DIVIDE) {
            derivation.remove(derivation.size() - 1); // pop the 16 rule from the derivation
            derivation.add(15); // Rule 15: <Term> → <Factor> / <Term>   ICI ON FAIT FACTOR / TERM et NON PAS TERM / FACTOR !!!!!
            parseOp();
            parseTerm();
        } 
        else {
            //error("Unexpected token in <Term>");
        }
    }

    public void parseFactor() {
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
            //error("Unexpected token in <ExprArith>");
        }
    }

    public void parseOp() {
        if (currentToken.getType() == LexicalUnit.PLUS) {
            derivation.add(16); // Rule 16: <Op> → +
            match(LexicalUnit.PLUS);
        } else if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(17); // Rule 17: <Op> → -
            match(LexicalUnit.MINUS);
        } else if (currentToken.getType() == LexicalUnit.TIMES) {
            derivation.add(18); // Rule 18: <Op> → *
            match(LexicalUnit.TIMES);
        } else if (currentToken.getType() == LexicalUnit.DIVIDE) {
            derivation.add(19); // Rule 19: <Op> → /
            match(LexicalUnit.DIVIDE);
        } else {
            //error("Unexpected token in <Op>");
        }
    }

    public void parseIf() {
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

    public void parseCond() {
        // PAS SUR DU IF DE LA REGLE 23
        if (currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(23); // Rule 23: <Cond> → <ExprArith> <Comp> <ExprArith>
            parseExprArith();
            parseComp();
            parseExprArith();
        } else if (currentToken.getType() == LexicalUnit.LPAREN) {
            derivation.add(25); // Rule 25: <Cond> → ( <Cond> )
            match(LexicalUnit.LPAREN);
            parseCond();
            match(LexicalUnit.RPAREN); 
        }
        // REGLE 26 ET 27 A RAJOUTER
        else {
            // error("Unexpected token in <Cond>");
        }

    }

    public void parseComp() {
        if (currentToken.getType() == LexicalUnit.EQUAL) {
            derivation.add(28); // Rule 28: <Comp> → ==
            match(LexicalUnit.EQUAL);
        } else if (currentToken.getType() == LexicalUnit.SMALEQ) {
            derivation.add(29); // Rule 29: <Comp> → <=
            match(LexicalUnit.SMALEQ);
        } else if (currentToken.getType() == LexicalUnit.SMALLER) {
            derivation.add(30); // Rule 30: <Comp> → <
            match(LexicalUnit.SMALLER);
        } else {
            //error("Unexpected token in <Comp>");
        }
    }

    public void parseWhile() {
        derivation.add(31); // Rule 31: <While> → WHILE [Cond] REPEAT <Code> END
        match(LexicalUnit.WHILE);
        match(LexicalUnit.LBRACK);
        parseCond();
        match(LexicalUnit.RBRACK);
        match(LexicalUnit.REPEAT);
        parseCode();
        match(LexicalUnit.END);
    }

    public void parseOut() {
        derivation.add(32); // Rule 32: <Out> → OUT [VarName]
        match(LexicalUnit.OUTPUT);
        match(LexicalUnit.VARNAME);
    }

    public void parseIn() {
        derivation.add(33); // Rule 33: <In> → IN [VarName]
        match(LexicalUnit.INPUT);
        match(LexicalUnit.VARNAME);
    }

    // derivation of LL(1) parsing table

    // derivation of the parse tree

}
