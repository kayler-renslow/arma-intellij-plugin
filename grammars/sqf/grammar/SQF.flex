package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import java.util.Collections;

%%

%class SQFLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}


LOCAL_VAR = [_][:jletterdigit:]*
GLOBAL_VAR = [:jletter:] [:jletterdigit:]*

LINE_TERMINATOR = \r|\n|\r\n
INPUT_CHARACTER = [^\r\n]

WHITE_SPACE = ({LINE_TERMINATOR} | [ \t\f])+

DIGIT = [0-9]
DIGITS = {DIGIT}+

INTEGER_LITERAL = {DIGITS}
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT}+
DEC_EXPONENT = ({DEC_SIGNIFICAND} | {INTEGER_LITERAL}) [Ee] [+-]? {DIGIT}*
DEC_LITERAL = ({DEC_SIGNIFICAND} | {DEC_EXPONENT})

HEX_LITERAL = [0] [xX] [0]* {HEX_DIGIT} {1,8}
HEX_DIGIT   = [0-9a-fA-F]

ESCAPE_SEQUENCE = \\[^\r\n]

STRING_LITERAL = ("\"\""|"\""([^\"]+|\"\")+"\"") | ("''" | "'"([^']+|'')+"'")

BLOCK_COMMENT = \/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+\/

COMMENT_CONTENT = ( [^*] | \*+ [^/*] )*
BLOCK_COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "/*" {COMMENT_CONTENT} "*"+ "/"

INLINE_COMMENT = "//" {INPUT_CHARACTER}*

MACRO_NEWLINE = ("\\\n" | "\\\r\n" | "\\\r") [ \t\f]*
MACRO_CHARACTER = [^\r\n] | {MACRO_NEWLINE}
MACRO_TEXT = {MACRO_CHARACTER}+
MACRO = "#" {MACRO_TEXT}
%%

<YYINITIAL> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {MACRO} { return TokenType.WHITE_SPACE; }

<YYINITIAL> {BLOCK_COMMENT} { return SQFTypes.BLOCK_COMMENT; }
<YYINITIAL> {INLINE_COMMENT} { return SQFTypes.INLINE_COMMENT; }

<YYINITIAL> {HEX_LITERAL} { return SQFTypes.HEX_LITERAL; }
<YYINITIAL> {INTEGER_LITERAL} { return SQFTypes.INTEGER_LITERAL; }
<YYINITIAL> {DEC_LITERAL} { return SQFTypes.DEC_LITERAL; }
<YYINITIAL> {STRING_LITERAL} { return SQFTypes.STRING_LITERAL; }


<YYINITIAL> "this" { return SQFTypes.LANG_VAR; }
<YYINITIAL> "_this" { return SQFTypes.LANG_VAR; }
<YYINITIAL> "_x" { return SQFTypes.LANG_VAR; }
<YYINITIAL> "_forEachIndex" { return SQFTypes.LANG_VAR; }
<YYINITIAL> "_exception" { return SQFTypes.LANG_VAR; }

<YYINITIAL> "case" { return SQFTypes.CASE; }


<YYINITIAL> {LOCAL_VAR} { return SQFTypes.LOCAL_VAR; }
<YYINITIAL> {GLOBAL_VAR} {
    int i = Collections.binarySearch(SQFStatic.LIST_COMMANDS, yytext(), SQFStatic.STRING_COMPARATOR);
    if(i < 0){
        return SQFTypes.GLOBAL_VAR;
    }
    return SQFTypes.COMMAND_TOKEN;
}

<YYINITIAL> "==" { return SQFTypes.EQEQ; }
<YYINITIAL> "!=" { return SQFTypes.NE; }
<YYINITIAL> ">>" { return SQFTypes.GTGT; }
<YYINITIAL> "<=" { return SQFTypes.LE; }
<YYINITIAL> ">=" { return SQFTypes.GE; }
<YYINITIAL> "&&" { return SQFTypes.AMPAMP; }
<YYINITIAL> "||" { return SQFTypes.BARBAR; }

<YYINITIAL> "*" { return SQFTypes.ASTERISK; }
<YYINITIAL> "=" { return SQFTypes.EQ; }
<YYINITIAL> "%" { return SQFTypes.PERC; }
<YYINITIAL> "+" { return SQFTypes.PLUS; }
<YYINITIAL> "-" { return SQFTypes.MINUS; }
<YYINITIAL> "/" { return SQFTypes.FSLASH; }
<YYINITIAL> "^" { return SQFTypes.CARET; }

<YYINITIAL> "<" { return SQFTypes.LT; }
<YYINITIAL> ">" { return SQFTypes.GT; }

<YYINITIAL> "!" { return SQFTypes.EXCL; }

<YYINITIAL> "("   { return SQFTypes.LPAREN; }
<YYINITIAL> ")"   { return SQFTypes.RPAREN; }
<YYINITIAL> "{"   { return SQFTypes.LBRACE; }
<YYINITIAL> "}"   { return SQFTypes.RBRACE; }
<YYINITIAL> "["   { return SQFTypes.LBRACKET; }
<YYINITIAL> "]"   { return SQFTypes.RBRACKET; }
<YYINITIAL> ","   { return SQFTypes.COMMA; }
<YYINITIAL> ";"   { return SQFTypes.SEMICOLON; }

<YYINITIAL> "?" { return SQFTypes.QUEST; }
<YYINITIAL> ":" { return SQFTypes.COLON; }

<YYINITIAL> . { return TokenType.BAD_CHARACTER; }