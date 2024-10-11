%%// Options of the scanner

%class LexicalAnalyzer	//Name
%unicode			//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%standalone		//Tell that Jflex don't use a parser

// Return value of the program

// Extended Regular Expressions

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

ProgName = {AlphaUpperCase}({Alpha}|_)*
VarName = {AlphaLowerCase}(AlphaNumeric)*
Number = {Numeric}+

%xstate YYINITIAL, SHORT_COMMENTS_STATE, LONG_COMMENTS_STATE

%%

//switch between mode, default : YYINITIAL

<YYINITIAL> {
    "$"         {yybegin(SHORT_COMMENTS_STATE);}
    "!!"        {yybegin(LONG_COMMENTS_STATE);}
}

<SHORT_COMMENTS_STATE> {
    "\n"         {yybegin(YYINITIAL);}
    [^"\n"]+     {}
}

<LONG_COMMENTS_STATE> {
    "!!"        {yybegin(YYINITIAL)}
    [^"!!"]+     {}
}


// Identification of tokens
