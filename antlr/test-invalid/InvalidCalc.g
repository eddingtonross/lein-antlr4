// Simple ANTLR grammar, borrowed from http://www.antlr.org/wiki/display/ANTLR3/Five+minute+introduction+to+ANTLR+3

// Chopped off the last few lines so this is an invalid grammar file.

grammar InvalidCalc;

PLUS  = '+' ;
MINUS = '-' ;
MULT  = '*' ;
DIV = '/' ;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

expr  : term ( ( PLUS | MINUS )  term )* ;

term  : factor ( ( MULT | DIV ) factor
