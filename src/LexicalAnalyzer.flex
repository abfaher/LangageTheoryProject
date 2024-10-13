
%% // Options of the scanner

%class LexicalAnalyzer	//Name
%unicode			//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%standalone		//Tell that Jflex don't use a parser

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

ProgName = {AlphaUpperCase}({Alpha}|_)*
VarName = {AlphaLowerCase}({AlphaNumeric})*
Number = {Numeric}+

%xstate YYINITIAL, SHORT_COMMENTS_STATE, LONG_COMMENTS_STATE

%% //Identification of tokens

<YYINITIAL> {
    // transition to comments states
    "$"         {yybegin(SHORT_COMMENTS_STATE);}
    "!!"        {yybegin(LONG_COMMENTS_STATE);}

    // handling LexicalUnit
    // utiliser LexicalUnit.LET ou LET ?????????????
    
    "LET"           { return new Symbol(LexicalUnit.LET, yyline, yycolumn, "LET"); }
    "BE"            { return new Symbol(LexicalUnit.BE, yyline, yycolumn, "BE"); }
    "END"           { return new Symbol(LexicalUnit.END, yyline, yycolumn, "END"); }
    ":"             { return new Symbol(LexicalUnit.COLUMN, yyline, yycolumn, ":"); }
    "="             { return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, "="); }
    "("             { return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, "("); }
    ")"             { return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, ")"); }
    "-"             { return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, "-"); }
    "+"             { return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, "+"); }
    "*"             { return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, "*"); }
    "/"             { return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, "/"); }
    "IF"            { return new Symbol(LexicalUnit.IF, yyline, yycolumn, "IF"); }
    "THEN"          { return new Symbol(LexicalUnit.THEN, yyline, yycolumn, "THEN"); }
    "ELSE"          { return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, "ELSE"); }
    "{"             { return new Symbol(LexicalUnit.LBRACK, yyline, yycolumn, "{"); }
    "}"             { return new Symbol(LexicalUnit.RBRACK, yyline, yycolumn, "}"); }
    "->"            { return new Symbol(LexicalUnit.IMPLIES, yyline, yycolumn, "->"); }
    "|"             { return new Symbol(LexicalUnit.PIPE, yyline, yycolumn, "|"); }
    "=="            { return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, "=="); }
    "<="            { return new Symbol(LexicalUnit.SMALEQ, yyline, yycolumn, "<="); }
    "<"             { return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, "<"); }
    "WHILE"         { return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, "WHILE"); }
    "REPEAT"        { return new Symbol(LexicalUnit.REPEAT, yyline, yycolumn, "REPEAT"); }
    "OUT"           { return new Symbol(LexicalUnit.OUTPUT, yyline, yycolumn, "OUT"); }
    "IN"            { return new Symbol(LexicalUnit.INPUT, yyline, yycolumn, "IN"); }
    {ProgName}      { return new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()); }
    {VarName}       { return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()); }
    {Number}        { return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()); }

}

<SHORT_COMMENTS_STATE> {
    "\n"         {yybegin(YYINITIAL);}
    [^"\n"]+     {}
}

<LONG_COMMENTS_STATE> {
    "!!"        {yybegin(YYINITIAL)}
    [^"!!"]+     {}
}
