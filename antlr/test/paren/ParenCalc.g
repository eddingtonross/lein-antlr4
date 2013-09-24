// Simple ANTLR grammar, borrowed from http://www.antlr.org/wiki/display/ANTLR3/Five+minute+introduction+to+ANTLR+3

// Now with parentheses!

grammar ParenCalc;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

prog : expr+ ;

expr  : term ( ( PLUS | MINUS )  term )* ;

term  : factor ( ( MULT | DIV ) factor )* ;

factor  : NUMBER | '(' expr ')' ;


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

PLUS  : '+' ;
MINUS : '-' ;
MULT  : '*' ;
DIV : '/' ;

NUMBER  : (DIGIT)+ ;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+  -> skip ;

fragment DIGIT  : '0'..'9' ;
