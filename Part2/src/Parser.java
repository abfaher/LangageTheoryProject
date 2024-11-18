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

    public void parseExprArith() {
        if (currentToken.getType() == LexicalUnit.VARNAME){
            derivation.add(11); // Rule 11: <ExprArith> → [VarName]
            match(LexicalUnit.VARNAME);
        } else if (currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(12); // Rule 12: <ExprArith> → [Number]
            match(LexicalUnit.NUMBER);
        } else if (currentToken.getType() == LexicalUnit.LPAREN) {
            derivation.add(13); // Rule 13: <ExprArith> → ( <ExprArith> )
            match(LexicalUnit.LPAREN);
            parseExprArith();
            match(LexicalUnit.RPAREN);
        } else if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(14); // Rule 14: <ExprArith> → - <ExprArith>
            match(LexicalUnit.MINUS);
            parseExprArith();
        }
        // Rule 15 is missing <ExprArith> → <ExprArith> + <ExprArith>, didn't figure out how to implement it...
        else {
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
            derivation.add(21); // Rule 21: <If> → IF [Cond] THEN <Code> ELSE <Code>
            parseCode();
        } else {
            derivation.add(20); // Rule 21: <If> → IF [Cond] THEN <Code>
        }
        match(LexicalUnit.END);
    }

    public void parseCond() {
        if (currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(24); // Rule 24: <Cond> → <ExprArith> <Comp> <ExprArith>
            parseExprArith();
            parseComp();
            parseExprArith();
        } else if (currentToken.getType() == LexicalUnit.PIPE) {
            match(LexicalUnit.PIPE);
            derivation.add(23); // Rule 25: <Cond> → <Cond> | <Cond>
            parseCond();
            match(LexicalUnit.PIPE);
            parseCond();
        }
        else {
            // error("Unexpected token in <Cond>");
        }

    }

    public void parseComp() {
        // Implement the parsing algorithm here
    }

    public void parseWhile() {
        // Implement the parsing algorithm here
    }

    public void parseOut() {
        // Implement the parsing algorithm here
    }

    public void parseIn() {
        // Implement the parsing algorithm here
    }


    // derivation of the grammar, left recursion and factorization handling

    
    // derivation of FIRST set
    
    // derivation if FOLLOW set

    // derivation of LL(1) parsing table

    // derivation of the parse tree

}
