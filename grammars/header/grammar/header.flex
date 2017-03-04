package com.kaylerrenslow.a3plugin.lang.header;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderParserDefinition;

%%

%class HeaderLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}

STRINGTABLE_ENTRY = "$STR_" [:jletter:] [:jletterdigit:]*
IDENTIFIER = [:jletter:] [:jletterdigit:]*

LINE_TERMINATOR = \r | \n | \r\n
INPUT_CHARACTER = [^\r\n]
MACRO_NEWLINE = ("\\\n" | "\\\r\n" | "\\\r") [ \t\f]*
MACRO_CHARACTER = [^\r\n] | {MACRO_NEWLINE}


WHITE_SPACE = ([ \t\f] | {LINE_TERMINATOR})+

BLOCK_COMMENT = "/*" ~"*/"
INLINE_COMMENT = "//" {INPUT_CHARACTER}*

DIGIT = [0-9]
DIGITS = {DIGIT}+

INTEGER_LITERAL = {DIGITS}
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT}+
DEC_EXPONENT = ({DEC_SIGNIFICAND} | {INTEGER_LITERAL}) [Ee] [+-]? {DIGIT}*
DEC_LITERAL = ({DEC_SIGNIFICAND} | {DEC_EXPONENT})

NUMBER_LITERAL = {INTEGER_LITERAL} | {DEC_LITERAL}

HEX_LITERAL = [0] [xX] [0]* {HEX_DIGIT} {1,8}
HEX_DIGIT   = [0-9a-fA-F]

ESCAPE_SEQUENCE = \\[^\r\n]

STRING_PART = "\"" ~"\"" //if you ever decide to allow single quotes for strings, you must go back and change the search for config function tags, since tag="tag" != tag='tag'
STRING_LITERAL = {STRING_PART}+


INCLUDE_VALUE_ANGBR = "<" ([^\r\n] | {ESCAPE_SEQUENCE})* ">"
FUNCTION_TAIL = {WHITE_SPACE}? "(" ([^\r\n()] | "(" [^\r\n()] ")")*? ")"
EVAL = "__EVAL" {FUNCTION_TAIL}
EXEC = "__EXEC" {FUNCTION_TAIL}

INCLUDE = "#include"

MACRO_CHARACTER = [^\r\n] | {MACRO_NEWLINE}
MACRO_TEXT = {MACRO_CHARACTER}+
MACRO = "#"("define"| "undef"| "ifdef"| "ifndef"| "else"| "endif") {LINE_TERMINATOR}? {MACRO_TEXT}?


%%

<YYINITIAL> {MACRO_NEWLINE} { return TokenType.WHITE_SPACE; }
<YYINITIAL> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }

<YYINITIAL> {BLOCK_COMMENT} { return HeaderParserDefinition.BLOCK_COMMENT; }
<YYINITIAL> {INLINE_COMMENT} { return HeaderParserDefinition.INLINE_COMMENT; }

<YYINITIAL> {NUMBER_LITERAL} { return HeaderTypes.NUMBER_LITERAL; }
<YYINITIAL> {HEX_LITERAL} { return HeaderTypes.HEX_LITERAL; }
<YYINITIAL> {STRING_LITERAL} { return HeaderTypes.STRING_LITERAL; }
<YYINITIAL> {INCLUDE_VALUE_ANGBR} { return HeaderTypes.INCLUDE_VALUE_ANGBR; }

<YYINITIAL> {STRINGTABLE_ENTRY} { return HeaderTypes.STRINGTABLE_ENTRY; }

<YYINITIAL> "class" { return HeaderTypes.CLASS; }

<YYINITIAL> {INCLUDE}  { return HeaderTypes.PREPROCESS_INCLUDE; }
<YYINITIAL> {MACRO}  { return HeaderTypes.PREPROCESS_MACRO; }
<YYINITIAL> {EXEC} { return HeaderTypes.PREPROCESS_EXEC; }
<YYINITIAL> {EVAL} { return HeaderTypes.PREPROCESS_EVAL; }

<YYINITIAL> {IDENTIFIER} { return HeaderTypes.IDENTIFIER; }

<YYINITIAL> "="   { return HeaderTypes.EQ; }

<YYINITIAL> "+="   { return HeaderTypes.PLUS_EQ; }
<YYINITIAL> "+"   { return HeaderTypes.PLUS; }
<YYINITIAL> "-"   { return HeaderTypes.MINUS; }
<YYINITIAL> "/"   { return HeaderTypes.FSLASH; }
<YYINITIAL> "*"   { return HeaderTypes.ASTERISK; }

<YYINITIAL> "{"   { return HeaderTypes.LBRACE; }
<YYINITIAL> "}"   { return HeaderTypes.RBRACE; }

<YYINITIAL> "("   { return HeaderTypes.LPAREN; }
<YYINITIAL> ")"   { return HeaderTypes.RPAREN; }


<YYINITIAL> "[]"   { return HeaderTypes.BRACKET_PAIR; }
<YYINITIAL> ","   { return HeaderTypes.COMMA; }
<YYINITIAL> ":"   { return HeaderTypes.COLON; }
<YYINITIAL> ";"   { return HeaderTypes.SEMICOLON; }

<YYINITIAL> . { return TokenType.BAD_CHARACTER; }
