import java.io.IOException;
import java.util.*;

public class Parser {

    private final LexicalAnalyzer scanner;
    private final List<Integer> derivation;
    private Symbol currentToken;

    public void printDerivation() {
        for(Integer elem: derivation){
            System.out.print(elem.toString() + " ");
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

    private void match(LexicalUnit expected) throws UnexpectedTokenException{
        if (currentToken.getType() == expected) {
            advance();
        } else {
            throw new UnexpectedTokenException(String.format("Unexpected token of type %s received, should have been type %s, at line %s and column %s", currentToken.getType().toString(), expected.toString(), currentToken.getLine(), currentToken.getColumn()));
        }
    }

    public ParseTree parseProgram() throws UnexpectedTokenException{
        ParseTree programNode = new ParseTree(new Symbol(null, 0, 0, "<Program>")); // Root node for <Program>
        advance();
        match(LexicalUnit.LET);
        derivation.add(1); // Rule 1: <Program> → LET [ProgName] BE <Code> END
        programNode.addChild(new ParseTree(new Symbol(LexicalUnit.LET)));
        
        match(LexicalUnit.PROGNAME);
        programNode.addChild(new ParseTree(currentToken)); // [ProgName]
    
        match(LexicalUnit.BE);
        programNode.addChild(new ParseTree(new Symbol(LexicalUnit.BE)));
        
        ParseTree codeNode = parseCode();
        programNode.addChild(codeNode); // Add the parsed <Code> subtree
        
        match(LexicalUnit.END);
        programNode.addChild(new ParseTree(new Symbol(LexicalUnit.END)));
    
        return programNode;
    }

    private ParseTree parseCode() throws UnexpectedTokenException{
        ParseTree codeNode = new ParseTree(new Symbol(null, 0, 0, "<Code>")); // Root node for <Code>
        if(currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.IF || currentToken.getType() == LexicalUnit.WHILE || currentToken.getType() == LexicalUnit.OUTPUT || currentToken.getType() == LexicalUnit.INPUT) {
            derivation.add(2); // Rule 2: <Code> → <Instruction> : <Code>
            ParseTree instructionNode = parseInstruction(); // Parse <Instruction>
            codeNode.addChild(instructionNode);
            match(LexicalUnit.COLUMN);
            codeNode.addChild(new ParseTree(new Symbol(LexicalUnit.COLUMN)));

            ParseTree nextCodeNode = parseCode(); // Parse <Code> recursively
            codeNode.addChild(nextCodeNode);
        }
        else if(currentToken.getType() == LexicalUnit.END || currentToken.getType() == LexicalUnit.ELSE){
            derivation.add(3); // Rule 3: → ϵ
            codeNode.addChild(new ParseTree(new Symbol(null, 0, 0, "ε")));
        }
        return codeNode;
    }

    private ParseTree parseInstruction() throws UnexpectedTokenException {
        ParseTree instructionNode = new ParseTree(new Symbol(null, 0, 0, "<Instruction>"));
    
        if (currentToken.getType() == LexicalUnit.VARNAME) {
            derivation.add(4); // Rule 4: <Instruction> → <Assign>
            instructionNode.addChild(parseAssign());
    
        } else if (currentToken.getType() == LexicalUnit.IF) {
            derivation.add(5); // Rule 5: <Instruction> → <If>
            instructionNode.addChild(parseIf());
    
        } else if (currentToken.getType() == LexicalUnit.WHILE) {
            derivation.add(6); // Rule 6: <Instruction> → <While>
            instructionNode.addChild(parseWhile());
            
        } // TODO : add the rule 7 <CALL>
        else if (currentToken.getType() == LexicalUnit.OUTPUT) {
            derivation.add(8); // Rule 8: <Instruction> → <Out>
            instructionNode.addChild(parseOut());
    
        } else if (currentToken.getType() == LexicalUnit.INPUT) {
            derivation.add(9); // Rule 9: <Instruction> → <In>
            instructionNode.addChild(parseIn());
        } else {
            throw new UnexpectedTokenException("Unexpected token in <Instruction>");
        }
    
        return instructionNode; // Return the constructed <Instruction> node
    }

    private ParseTree parseAssign() throws UnexpectedTokenException {
        ParseTree assignNode = new ParseTree(new Symbol(null, 0, 0, "<Assign>"));
    
        match(LexicalUnit.VARNAME);
        assignNode.addChild(new ParseTree(currentToken));
    
        match(LexicalUnit.ASSIGN);
        assignNode.addChild(new ParseTree(new Symbol(LexicalUnit.ASSIGN)));
    
        ParseTree exprNode = parseExprArith();
        assignNode.addChild(exprNode);
    
        return assignNode;
    }

    private ParseTree parseAtomCond() throws UnexpectedTokenException{
        ParseTree atomCondNode = new ParseTree(new Symbol(null, 0, 0, "<AtomCond>"));
        if (currentToken.getType() == LexicalUnit.PIPE) {
            derivation.add(19); // Rule 19: <AtomCond> → |<Cond>|
            match(LexicalUnit.PIPE);
            atomCondNode.addChild(new ParseTree(new Symbol(LexicalUnit.PIPE)));
            atomCondNode.addChild(parseCond());
            match(LexicalUnit.PIPE);
            atomCondNode.addChild(new ParseTree(new Symbol(LexicalUnit.PIPE)));
        }
        // } else if (currentToken.getType() == LexicalUnit.MINUS || currentToken.getType() == LexicalUnit.VARNAME || currentToken.getType() == LexicalUnit.NUMBER || currentToken.getType() == LexicalUnit.LPAREN){
        else{
            derivation.add(20); // Rule 20: <AtomCond> → <ExprArith>
            atomCondNode.addChild(parseExprArith());
        }

        return atomCondNode;
    }

    private ParseTree parseCompCondPrime () throws UnexpectedTokenException{
        ParseTree compCondPrimeNode = new ParseTree(new Symbol(null, 0, 0, "<CompCondPrime>"));
        if (currentToken.getType() == LexicalUnit.EQUAL) {
            derivation.add(15); // Rule 15: <CompCondPrime> → ==
            match(LexicalUnit.EQUAL);
            compCondPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.EQUAL)));
            compCondPrimeNode.addChild(parseAtomCond());
            compCondPrimeNode.addChild(parseCompCondPrime());
        } else if (currentToken.getType() == LexicalUnit.SMALEQ) {
            derivation.add(16); // Rule 16: <CompCondPrime> → <=
            match(LexicalUnit.SMALEQ);
            compCondPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.SMALEQ)));
            compCondPrimeNode.addChild(parseAtomCond());
            compCondPrimeNode.addChild(parseCompCondPrime());
        } else if (currentToken.getType() == LexicalUnit.SMALLER) {
            derivation.add(17); // Rule 17: <CompCondPrime> → <
            match(LexicalUnit.SMALLER);
            compCondPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.SMALLER)));
            compCondPrimeNode.addChild(parseAtomCond());
            compCondPrimeNode.addChild(parseCompCondPrime());
        } else if (currentToken.getType() == LexicalUnit.IMPLIES || currentToken.getType() == LexicalUnit.PIPE || currentToken.getType() == LexicalUnit.RBRACK){
            // Rule 18: <CompCondPrime> → ε
            derivation.add(18);
            compCondPrimeNode.addChild(new ParseTree(new Symbol(null, 0, 0, "ε")));
        }

        return compCondPrimeNode;
    }

    private ParseTree parseCompCond () throws UnexpectedTokenException{
        ParseTree compCondNode = new ParseTree(new Symbol(null, 0, 0, "<CompCond>"));
        derivation.add(14); // Rule 14: <CompCond> → <AtomCond> <CompCondPrime>
        compCondNode.addChild(parseAtomCond());
        compCondNode.addChild(parseCompCondPrime());

        return compCondNode;
    }

    private ParseTree parseCondTail() throws UnexpectedTokenException{
        ParseTree condTailNode = new ParseTree(new Symbol(null, 0, 0, "<CondTail>"));
        if (currentToken.getType() == LexicalUnit.IMPLIES) {
            derivation.add(12); // Rule 12: <CondTail> → -> <Cond>
            match(LexicalUnit.IMPLIES);
            condTailNode.addChild(new ParseTree(new Symbol(LexicalUnit.IMPLIES)));
            condTailNode.addChild(parseCond());
        } else if (currentToken.getType() == LexicalUnit.PIPE || currentToken.getType() == LexicalUnit.RBRACK) {
            // Rule 13: <CondTail> → ε
            derivation.add(13);
            condTailNode.addChild(new ParseTree(new Symbol(null, 0, 0, "ε")));
        }

        return condTailNode;
    }

    private ParseTree parseCond() throws UnexpectedTokenException{
        ParseTree condNode = new ParseTree(new Symbol(null, 0, 0, "<Cond>"));
        derivation.add(11) ; // Rule 11: <Cond> → <CompCond> <CondTail>
        condNode.addChild(parseCompCond());
        condNode.addChild(parseCondTail());

        return condNode;
    }

    private ParseTree parseAtom() throws UnexpectedTokenException {
        ParseTree atomNode = new ParseTree(new Symbol(null, 0, 0, "<Atom>"));
    
        if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(29); // Rule 29: <Atom> → - <Atom>
            match(LexicalUnit.MINUS);
            atomNode.addChild(new ParseTree(new Symbol(LexicalUnit.MINUS)));
            atomNode.addChild(parseAtom()); // Recursive <Atom>
    
        } else if (currentToken.getType() == LexicalUnit.VARNAME) {
            derivation.add(30); // Rule 30: <Atom> → [VarName]
            match(LexicalUnit.VARNAME);
            atomNode.addChild(new ParseTree(new Symbol(LexicalUnit.VARNAME))); // [VarName]
    
        } else if (currentToken.getType() == LexicalUnit.NUMBER) {
            derivation.add(31); // Rule 31: <Atom> → [Number]
            match(LexicalUnit.NUMBER);
            atomNode.addChild(new ParseTree(new Symbol(LexicalUnit.NUMBER)));
    
        } else if (currentToken.getType() == LexicalUnit.LPAREN) {
            derivation.add(32); // Rule 26: <Atom> → ( <ExprArith> )
            match(LexicalUnit.LPAREN);
            atomNode.addChild(new ParseTree(new Symbol(LexicalUnit.LPAREN)));
    
            atomNode.addChild(parseExprArith()); // Add <ExprArith>
    
            match(LexicalUnit.RPAREN);
            atomNode.addChild(new ParseTree(new Symbol(LexicalUnit.RPAREN)));
        }
    
        return atomNode;
    }

    private ParseTree parseProdPrime() throws UnexpectedTokenException{
        ParseTree prodPrimeNode = new ParseTree(new Symbol(null, 0, 0, "<ProdPrime>"));
        if (currentToken.getType() == LexicalUnit.TIMES) {
            derivation.add(26); // Rule 26: <ProdPrime> → * <Atom> <ProdPrime>
            match(LexicalUnit.TIMES);
            prodPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.TIMES)));
            prodPrimeNode.addChild(parseAtom());
            prodPrimeNode.addChild(parseProdPrime());
        } else if (currentToken.getType() == LexicalUnit.DIVIDE) {
            derivation.add(27); // Rule 27: <ProdPrime> → / <Atom> <ProdPrime>
            match(LexicalUnit.DIVIDE);
            prodPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.DIVIDE)));
            prodPrimeNode.addChild(parseAtom());
            prodPrimeNode.addChild(parseProdPrime());
        } else if (currentToken.getType() == LexicalUnit.PLUS || currentToken.getType() == LexicalUnit.MINUS || currentToken.getType() == LexicalUnit.COLUMN || currentToken.getType() == LexicalUnit.RPAREN || currentToken.getType() == LexicalUnit.IMPLIES || currentToken.getType() == LexicalUnit.PIPE || currentToken.getType() == LexicalUnit.RBRACK){
            // Rule 28: <ProdPrime> → ε
            derivation.add(28);
            prodPrimeNode.addChild(new ParseTree(new Symbol(null, 0, 0, "ε")));
        }

        return prodPrimeNode;
    }

    private ParseTree parseProd() throws UnexpectedTokenException {
        ParseTree prodNode = new ParseTree(new Symbol(null, 0, 0, "<Prod>"));
        derivation.add(25); // Rule 25: <Prod> → <Atom> <ProdPrime>
    
        ParseTree atomNode = parseAtom();
        prodNode.addChild(atomNode); // Add <Atom>
    
        ParseTree prodPrimeNode = parseProdPrime();
        prodNode.addChild(prodPrimeNode); // Add <ProdPrime>
    
        return prodNode; // Return the constructed <Prod> node
    }

    private ParseTree parseExprArithPrime() throws UnexpectedTokenException{
        ParseTree exprArithPrimeNode = new ParseTree(new Symbol(null, 0, 0, "<ExprArithPrime>"));
        if (currentToken.getType() == LexicalUnit.PLUS) {
            derivation.add(22); // Rule 22: <ExprArithPrime> → + <Prod> <ExprArithPrime>
            match(LexicalUnit.PLUS);
            exprArithPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.PLUS)));
            exprArithPrimeNode.addChild(parseProd());
            exprArithPrimeNode.addChild(parseExprArithPrime());
        } else if (currentToken.getType() == LexicalUnit.MINUS) {
            derivation.add(23); // Rule 23: <ExprArithPrime> → - <Prod> <ExprArithPrime>
            match(LexicalUnit.MINUS);
            exprArithPrimeNode.addChild(new ParseTree(new Symbol(LexicalUnit.MINUS)));
            exprArithPrimeNode.addChild(parseProd());
            exprArithPrimeNode.addChild(parseExprArithPrime());
        } else if (currentToken.getType() == LexicalUnit.RPAREN || currentToken.getType() == LexicalUnit.EQUAL || currentToken.getType() == LexicalUnit.SMALEQ || currentToken.getType() == LexicalUnit.SMALLER || currentToken.getType() == LexicalUnit.IMPLIES || currentToken.getType() == LexicalUnit.PIPE || currentToken.getType() == LexicalUnit.RBRACK || currentToken.getType() == LexicalUnit.COLUMN){
            // Rule 24: <ExprArithPrime> → ε
            derivation.add(24);
            exprArithPrimeNode.addChild(new ParseTree(new Symbol(null, 0, 0, "ε")));
        }

        return exprArithPrimeNode;
    }

    private ParseTree parseExprArith() throws UnexpectedTokenException{
        ParseTree exprArithNode = new ParseTree(new Symbol(null, 0, 0, "<ExprArith>"));
        derivation.add(21); // Rule 21: <ExprArith> → <Prod> <ExprArithPrime>
        exprArithNode.addChild(parseProd());
        exprArithNode.addChild(parseExprArithPrime());

        return exprArithNode;
    }

    private ParseTree parseIfTail() throws UnexpectedTokenException{
        ParseTree ifTailNode = new ParseTree(new Symbol(null, 0, 0, "<IfTail>"));
        if (currentToken.getType() == LexicalUnit.END) {
            derivation.add(34); // Rule 34: <IfTail> → END
            match(LexicalUnit.END);
            ifTailNode.addChild(new ParseTree(new Symbol(LexicalUnit.END)));
        } else if (currentToken.getType() == LexicalUnit.ELSE) {
            derivation.add(35); // Rule 35: <IfTail> → ELSE <Code>
            match(LexicalUnit.ELSE);
            ifTailNode.addChild(new ParseTree(new Symbol(LexicalUnit.ELSE)));
            ifTailNode.addChild(parseCode());
            match(LexicalUnit.END);
            ifTailNode.addChild(new ParseTree(new Symbol(LexicalUnit.END)));
        }

        return ifTailNode;
    }

    private ParseTree parseIf() throws UnexpectedTokenException{
        ParseTree ifNode = new ParseTree(new Symbol(null, 0, 0, "<If>"));
        derivation.add(33); // Rule 30: <While> → WHILE [Cond] REPEAT <Code> END
        match(LexicalUnit.IF);
        ifNode.addChild(new ParseTree(new Symbol(LexicalUnit.IF)));
        match(LexicalUnit.LBRACK);
        ifNode.addChild(new ParseTree(new Symbol(LexicalUnit.LBRACK)));
        ifNode.addChild(parseCond());
        match(LexicalUnit.RBRACK);
        ifNode.addChild(new ParseTree(new Symbol(LexicalUnit.RBRACK)));
        match(LexicalUnit.THEN);
        ifNode.addChild(new ParseTree(new Symbol(LexicalUnit.THEN)));
        ifNode.addChild(parseCode());
        ifNode.addChild(parseIfTail());

        return ifNode;
    }

    private ParseTree parseWhile() throws UnexpectedTokenException{
        ParseTree whileNode = new ParseTree(new Symbol(null, 0, 0, "<While>"));
        derivation.add(36); // Rule 36: <While> → WHILE [Cond] REPEAT <Code> END
        match(LexicalUnit.WHILE);
        whileNode.addChild(new ParseTree(new Symbol(LexicalUnit.WHILE)));
        match(LexicalUnit.LBRACK);
        whileNode.addChild(new ParseTree(new Symbol(LexicalUnit.LBRACK)));
        whileNode.addChild(parseCond());
        match(LexicalUnit.RBRACK);
        whileNode.addChild(new ParseTree(new Symbol(LexicalUnit.RBRACK)));
        match(LexicalUnit.REPEAT);
        whileNode.addChild(new ParseTree(new Symbol(LexicalUnit.REPEAT)));
        whileNode.addChild(parseCode());
        match(LexicalUnit.END);
        whileNode.addChild(new ParseTree(new Symbol(LexicalUnit.END)));
        
        return whileNode;
    }

    private ParseTree parseOut() throws UnexpectedTokenException{
        ParseTree outNode = new ParseTree(new Symbol(null, 0, 0, "<Out>"));
        derivation.add(37); // Rule 37: <Out> → OUT [VarName]
        match(LexicalUnit.OUTPUT);
        outNode.addChild(new ParseTree(new Symbol(LexicalUnit.OUTPUT)));
        match(LexicalUnit.LPAREN);
        outNode.addChild(new ParseTree(new Symbol(LexicalUnit.LPAREN)));
        match(LexicalUnit.VARNAME);
        outNode.addChild(new ParseTree(new Symbol(LexicalUnit.VARNAME)));
        match(LexicalUnit.RPAREN);
        outNode.addChild(new ParseTree(new Symbol(LexicalUnit.RPAREN)));

        return outNode;
    }

    private ParseTree parseIn() throws UnexpectedTokenException{
        ParseTree inNode = new ParseTree(new Symbol(null, 0, 0, "<In>"));
        derivation.add(38); // Rule 33: <In> → IN [VarName]
        match(LexicalUnit.INPUT);
        inNode.addChild(new ParseTree(new Symbol(LexicalUnit.INPUT)));
        match(LexicalUnit.LPAREN);
        inNode.addChild(new ParseTree(new Symbol(LexicalUnit.LPAREN)));
        match(LexicalUnit.VARNAME);
        inNode.addChild(new ParseTree(new Symbol(LexicalUnit.VARNAME)));
        match(LexicalUnit.RPAREN);
        inNode.addChild(new ParseTree(new Symbol(LexicalUnit.RPAREN)));

        return inNode;
    }


}
