//package src;
%%// Options of the scanner

%class LexicalAnalyzer	//Name
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

%xstate YYINITIAL, SHORT_COMMENTS_STATE, LONG_COMMENTS_STATE, PROGNAME_STATE

%%// Identification of tokens


<YYINITIAL> {
    "$"     {yybegin(SHORT_COMMENTS_STATE);}
    "!!"     {yybegin(LONG_COMMENTS_STATE);}

    "LET"   {Symbol token = new Symbol(LexicalUnit.LET, yyline, yycolumn, yytext()); yybegin(PROGNAME_STATE); return token; }
    "BE"   {return new Symbol(LexicalUnit.BE, yyline, yycolumn, yytext());}
    "END"   {return new Symbol(LexicalUnit.END, yyline, yycolumn, yytext());}
    ":"   {return new Symbol(LexicalUnit.COLUMN, yyline, yycolumn, yytext());}
    "="   {return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
    "("   {return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
    ")"   {return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}
    "-"   {return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
    "+"   {return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
    "*"   {return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
    "/"   {return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}
    "IF"   {return new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
    "THEN"   {return new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
    "ELSE"   {return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}
    "{"   {return new Symbol(LexicalUnit.LBRACK, yyline, yycolumn, yytext());}
    "}"   {return new Symbol(LexicalUnit.RBRACK, yyline, yycolumn, yytext());}
    "->"   {return new Symbol(LexicalUnit.IMPLIES, yyline, yycolumn, yytext());}
    "|"   {return new Symbol(LexicalUnit.PIPE, yyline, yycolumn, yytext());}
    "=="   {return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
    "<="   {return new Symbol(LexicalUnit.SMALEQ, yyline, yycolumn, yytext());}
    "<"   {return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}
    "WHILE"   {return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
    "REPEAT"   {return new Symbol(LexicalUnit.REPEAT, yyline, yycolumn, yytext());}
    "OUT"   {return new Symbol(LexicalUnit.OUTPUT, yyline, yycolumn, yytext());}
    "IN"   {return new Symbol(LexicalUnit.INPUT, yyline, yycolumn, yytext());}

    {ProgName} {return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());}
    {VarName} {return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());}
    {Number} {return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, Integer.parseInt(yytext()));}

    {Space} {} // Ignore spaces
    {EndOfLine} {} // Ignore the newline character

}

<SHORT_COMMENTS_STATE> {
    {EndOfLine} {yybegin(YYINITIAL);}
    . {} // Ignore every character in the comment except the newline character
}

<LONG_COMMENTS_STATE> {
    "!!" {yybegin(YYINITIAL);}
    . {} // Ignore every character in the comment except the newline character
    {EndOfLine} {} // Ignore the newline character
}


<PROGNAME_STATE> {
    {Space} {}
    {EndOfLine} {yybegin(YYINITIAL);}
    {ProgName} {Symbol token = new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()); yybegin(YYINITIAL); return token; }
}

