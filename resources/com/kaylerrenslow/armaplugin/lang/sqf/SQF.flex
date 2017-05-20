package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFParserDefinition;
import java.util.Collections;

%%

%public %class SQFLexer
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


STRING_LITERAL = ("\"\""|"\""([^\"]+|\"\")+"\"") | ("''" | "'"([^']+|'')+"'")

BLOCK_COMMENT = \/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+\/

COMMENT_CONTENT = ( [^*] | \*+ [^/*] )*
BLOCK_COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "/*" {COMMENT_CONTENT} "*"+ "/"

INLINE_COMMENT = "//" {INPUT_CHARACTER}*

MACRO_CHARACTER = [^\r\n] | (("\\\n" | "\\\r\n" | "\\\r") [ \t\f]*)
MACRO_TEXT = {MACRO_CHARACTER}+
MACRO = "#"("define"| "undef"| "ifdef"| "ifndef"| "else"| "endif") {MACRO_TEXT}?

%%

<YYINITIAL> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {MACRO} { return TokenType.WHITE_SPACE; }

<YYINITIAL> {BLOCK_COMMENT} { return SQFParserDefinition.BLOCK_COMMENT; }
<YYINITIAL> {INLINE_COMMENT} { return SQFParserDefinition.INLINE_COMMENT; }

<YYINITIAL> {HEX_LITERAL} { return SQFTypes.HEX_LITERAL; }
<YYINITIAL> {INTEGER_LITERAL} { return SQFTypes.INTEGER_LITERAL; }
<YYINITIAL> {DEC_LITERAL} { return SQFTypes.DEC_LITERAL; }
<YYINITIAL> {STRING_LITERAL} { return SQFTypes.STRING_LITERAL; }

<YYINITIAL> {LOCAL_VAR} { return SQFTypes.LOCAL_VAR; }
<YYINITIAL> {GLOBAL_VAR} {
    for(String command : SQFStatic.LIST_COMMANDS){  //don't use binary search so that we can do ignore case search
        if(command.equalsIgnoreCase(yytext().toString())){
            return SQFTypes.COMMAND_TOKEN;
        }
    }
    return SQFTypes.GLOBAL_VAR;
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
<YYINITIAL> "{"   { return SQFTypes.L_CURLY_BRACE; }
<YYINITIAL> "}"   { return SQFTypes.R_CURLY_BRACE; }
<YYINITIAL> "["   { return SQFTypes.L_SQ_BRACKET; }
<YYINITIAL> "]"   { return SQFTypes.R_SQ_BRACKET; }
<YYINITIAL> ","   { return SQFTypes.COMMA; }
<YYINITIAL> ";"   { return SQFTypes.SEMICOLON; }

<YYINITIAL> "?" { return SQFTypes.QUEST; }
<YYINITIAL> ":" { return SQFTypes.COLON; }

<YYINITIAL> . { return TokenType.BAD_CHARACTER; }