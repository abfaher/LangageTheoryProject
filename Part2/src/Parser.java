import java.io.IOException;
import java.util.*;

public class Parser {

    private final LexicalAnalyzer scanner;
    private final List<Integer> derivation;
    private Symbol currentToken;
    public void printDerivation() {
        for(Integer elem: derivation){
            System.out.println(elem.toString() + " ");
        }

    }
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
        advance();
        derivation.add(1); // Rule 1: <Program> → LET [ProgName] BE <Code> END
        match(LexicalUnit.LET);
        match(LexicalUnit.PROGNAME);
        match(LexicalUnit.BE);
        parseCode();
        match(LexicalUnit.END);
    }

    private void parseCode() {
        if(currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.IF || currentToken.getType() == LexicalUnit.WHILE || currentToken.getType() == LexicalUnit.OUTPUT || currentToken.getType() == LexicalUnit.INPUT) {
            derivation.add(2); // Rule 2: <Code> → <Instruction> : <Code>
            parseInstruction();
            match(LexicalUnit.COLUMN);
            parseCode();
        }
        else{
            derivation.add(3); // Rule 2: <Code> → <Instruction> : <Code>

        }


        // TODO: Rule 3 is missing <Code> → ε
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
        }
    }

    private void parseAssignment() {
        derivation.add(10); // Rule 10: <Assignment> → [VarName] = <ExprArith>
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        parseExprArith();
    }

    private void parseAtomCond() {
        if (currentToken.getType() == LexicalUnit.PIPE) {
            derivation.add(19); // Rule 19: <AtomCond> → |<Cond>|
            match(LexicalUnit.PIPE);
            parseCond();
            match(LexicalUnit.PIPE);
        } else if (currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.NUMBER || currentToken.getType() == LexicalUnit.MINUS) {
           derivation.add(20); // Rule 20: <AtomCond> → <ExprArith>
            parseExprArith();
        }
    }

    private void parseCompCondPrime () {
        if (currentToken.getType() == LexicalUnit.EQUAL) {
            derivation.add(15); // Rule 15: <CompCondPrime> → ==
            match(LexicalUnit.EQUAL);
            parseAtomCond();
            parseCompCondPrime();
        } else if (currentToken.getType() == LexicalUnit.SMALEQ) {
            derivation.add(16); // Rule 16: <CompCondPrime> → <=
            match(LexicalUnit.SMALEQ);
            parseAtomCond();
            parseCompCondPrime();
        } else if (currentToken.getType() == LexicalUnit.SMALLER) {
            derivation.add(17); // Rule 17: <CompCondPrime> → <
            match(LexicalUnit.SMALLER);
            parseAtomCond();
            parseCompCondPrime();
        } else {
            // Rule 18: <CompCondPrime> → ε
            derivation.add(18);
        }
    }

    private void parseCompCond () {
        derivation.add(14); // Rule 14: <CompCond> → <AtomCond> <CompCondPrime>
        parseAtomCond();
        parseCompCondPrime();
    }

    private void parseCondTail() {
        if (currentToken.getType() == LexicalUnit.IMPLIES) {
            derivation.add(12); // Rule 12: <CondTail> → -> <Cond>
            match(LexicalUnit.IMPLIES);
            parseCond();
        } else {
            // Rule 13: <CondTail> → ε
            derivation.add(13);
        }
    }

    private void parseCond() {
        derivation.add(11) ; // Rule 11: <Cond> → <CompCond> <CondTail>
        parseCompCond();
        parseCondTail();
    }

    private void parseAtom() {
        if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(29); // Rule 29: <Atom> → - <Atom>
            match(LexicalUnit.MINUS);
            parseAtom();
        } else if (currentToken.getType() == LexicalUnit.VARNAME) {
            derivation.add(30); // Rule 30: <Atom> → [VarName]
            match(LexicalUnit.VARNAME);
        } else if (currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(31); // Rule 31: <Atom> → [Number]
            match(LexicalUnit.NUMBER);
        } else if (currentToken.getType() == LexicalUnit.LPAREN) {
            derivation.add(32); // Rule 26: <Atom> → ( <ExprArith> )
            match(LexicalUnit.LPAREN);
            parseExprArith();
            match(LexicalUnit.RPAREN);
        }
    }

    private void parseProdPrime() {
        if (currentToken.getType() == LexicalUnit.TIMES) {
            derivation.add(26); // Rule 26: <ProdPrime> → * <Atom> <ProdPrime>
            match(LexicalUnit.TIMES);
            parseAtom();
            parseProdPrime();
        } else if (currentToken.getType() == LexicalUnit.DIVIDE) {
            derivation.add(27); // Rule 27: <ProdPrime> → / <Atom> <ProdPrime>
            match(LexicalUnit.DIVIDE);
            parseAtom();
            parseProdPrime();
        } else {
            // Rule 28: <ProdPrime> → ε
            derivation.add(28);
        }
    }

    private void parseProd() {
        derivation.add(25); // Rule 25: <Prod> → <Atom> <ProdPrime>
        parseAtom();
        parseProdPrime();
    }

    private void parseExprArithPrime() {
        if (currentToken.getType() == LexicalUnit.PLUS) {
            derivation.add(22); // Rule 22: <ExprArithPrime> → + <Prod> <ExprArithPrime>
            match(LexicalUnit.PLUS);
            parseProd();
            parseExprArithPrime();
        } else if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(23); // Rule 23: <ExprArithPrime> → - <Prod> <ExprArithPrime>
            match(LexicalUnit.MINUS);
            parseProd();
            parseExprArithPrime();
        } else {
            // Rule 24: <ExprArithPrime> → ε
            derivation.add(24);
        }
    }

    private void parseExprArith() {
        derivation.add(21); // Rule 21: <ExprArith> → <Prod> <ExprArithPrime>
        parseProd();
        parseExprArithPrime();
    }

    private void parseIfTail() {
        if (currentToken.getType() == LexicalUnit.END) {
            derivation.add(34); // Rule 24: <IfTail> → ELSE <Code>
            match(LexicalUnit.END);
        } else if (currentToken.getType() == LexicalUnit.ELSE) {
            derivation.add(35); // Rule 24: <IfTail> → ELSE <Code>
            match(LexicalUnit.ELSE);
            parseCode();
            match(LexicalUnit.END);
        }
    }

    private void parseIf() {
        match(LexicalUnit.IF);
        match(LexicalUnit.LBRACK);
        parseCond();
        match(LexicalUnit.RBRACK);
        match(LexicalUnit.THEN);
        parseCode();
        parseIfTail();
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
