%%// Options of the scanner

%class Lexer5	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%standalone		//Standalone mode

// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

Space				= "\t" | " "
EndOfLine		= "\r"?"\n"

ProgName       = {AlphaUpperCase}({Alpha}|"_")*
VarName        = {AlphaLowerCase}{AlphaNumeric}*
Number         = (0|[1-9]{Numeric}*)

%xstate YYINITIAL, SHORT_COMMENTS_STATE, LONG_COMMENTS_STATE

%%// Identification of tokens


<YYINITIAL> {
    "$"     {yybegin(SHORT_COMMENTS_STATE);}
    "!!"     {yybegin(LONG_COMMENTS_STATE);}

    "LET"   {Symbol symbol = new Symbol(LexicalUnit.LET, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "BE"   {Symbol symbol = new Symbol(LexicalUnit.BE, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "END"   {Symbol symbol = new Symbol(LexicalUnit.END, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    ":"   {Symbol symbol = new Symbol(LexicalUnit.COLUMN, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "="   {Symbol symbol = new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "("   {Symbol symbol = new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    ")"   {Symbol symbol = new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "-"   {Symbol symbol = new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "+"   {Symbol symbol = new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "*"   {Symbol symbol = new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "/"   {Symbol symbol = new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "IF"   {Symbol symbol = new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "THEN"   {Symbol symbol = new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "ELSE"   {Symbol symbol = new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "{"   {Symbol symbol = new Symbol(LexicalUnit.LBRACK, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "}"   {Symbol symbol = new Symbol(LexicalUnit.RBRACK, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "->"   {Symbol symbol = new Symbol(LexicalUnit.IMPLIES, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "|"   {Symbol symbol = new Symbol(LexicalUnit.PIPE, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "=="   {Symbol symbol = new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "<="   {Symbol symbol = new Symbol(LexicalUnit.SMALEQ, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "<"   {Symbol symbol = new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "WHILE"   {Symbol symbol = new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "REPEAT"   {Symbol symbol = new Symbol(LexicalUnit.REPEAT, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "OUT"   {Symbol symbol = new Symbol(LexicalUnit.OUTPUT, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    "IN"   {Symbol symbol = new Symbol(LexicalUnit.INPUT, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}

    {ProgName} {Symbol symbol = new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    {VarName} {Symbol symbol = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()); System.out.println(symbol); return symbol;}
    {Number} {Symbol symbol = new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, Integer.parseInt(yytext())); System.out.println(symbol); return symbol;}


}


<SHORT_COMMENTS_STATE> {
    {EndOfLine} {yybegin(YYINITIAL);}
    . {} // Ignore every character in the comment
}

<LONG_COMMENTS_STATE> {
    "!!" {yybegin(YYINITIAL);}
    . {} // Ignore every character in the comment
}

