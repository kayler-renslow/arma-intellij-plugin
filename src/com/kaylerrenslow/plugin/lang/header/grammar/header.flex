package com.kaylerrenslow.plugin.lang.header;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.plugin.lang.header.psi.HeaderTypes;
import com.intellij.psi.impl.source.tree.JavaDocElementType;

%%

%class HeaderLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}

IDENTIFIER = [:jletter:] [:jletterdigit:]*

LINE_TERMINATOR = \r | \n | \r\n
INPUT_CHARACTER = [^\r\n]

IGNORE = (" \\\n" | " \\\r\n" | " \\\r") [ \t\f]*

WHITE_SPACE = [ \t\f] | {LINE_TERMINATOR}

COMMENT = {TRADITIONAL_COMMENT} | {END_OF_LINE_COMMENT}

TRADITIONAL_COMMENT = "/*" ~"*/" | "/*" "*"+ "/"
END_OF_LINE_COMMENT = "//" {INPUT_CHARACTER}* {LINE_TERMINATOR}?


DIGIT = [0-9]
DIGITS = {DIGIT}+

INTEGER_LITERAL = {DIGITS}
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT}+
DEC_EXPONENT = ({DEC_SIGNIFICAND} | {INTEGER_LITERAL}) [Ee] [+-]? {DIGIT}*
DEC_LITERAL = ({DEC_SIGNIFICAND} | {DEC_EXPONENT})

NUMBER_LITERAL = {INTEGER_LITERAL} | {DEC_LITERAL}

ESCAPE_SEQUENCE = \\[^\r\n]

STRING_PART = "\"" ~"\""
STRING_LITERAL = {STRING_PART}+

INCLUDE_VALUE_ANGBR = "<" ([^\r\n] | {ESCAPE_SEQUENCE})* ">"

%%

<YYINITIAL> {IGNORE} { return HeaderTypes.WHITE_SPACE; }
<YYINITIAL> {WHITE_SPACE}+ { return HeaderTypes.WHITE_SPACE; }


<YYINITIAL> {COMMENT} { return HeaderTypes.COMMENT; }
<YYINITIAL> {END_OF_LINE_COMMENT} { return HeaderTypes.COMMENT; }

<YYINITIAL> {NUMBER_LITERAL} { return HeaderTypes.NUMBER_LITERAL; }
<YYINITIAL> {STRING_LITERAL} { return HeaderTypes.STRING_LITERAL; }
<YYINITIAL> {INCLUDE_VALUE_ANGBR} { return HeaderTypes.INCLUDE_VALUE_ANGBR; }


<YYINITIAL> "class" { return HeaderTypes.CLASS; }

<YYINITIAL> "#include"  { return HeaderTypes.PREPROCESS_INCLUDE; }
/*
<YYINITIAL> "#define"  { return HeaderTypes.PREPROCESS_DEFINE; }
<YYINITIAL> "#undef"  { return HeaderTypes.PREPROCESS_UNDEF; }
<YYINITIAL> "#ifdef"  { return HeaderTypes.PREPROCESS_IF_DEF; }
<YYINITIAL> "#ifndef"  { return HeaderTypes.PREPROCESS_IF_N_DEF; }
<YYINITIAL> "#else"  { return HeaderTypes.PREPROCESS_ELSE; }
<YYINITIAL> "#endif"  { return HeaderTypes.PREPROCESS_END_IF; }
<YYINITIAL> "#hash"  { return HeaderTypes.PREPROCESS_HASH; }
<YYINITIAL> "##"  { return HeaderTypes.PREPROCESS_HASH_HASH; }
<YYINITIAL> "__EXEC"  { return HeaderTypes.PREPROCESS_EXEC; }
<YYINITIAL> "__EVAL"  { return HeaderTypes.PREPROCESS_EVAL; }
<YYINITIAL> "__LINE__"  { return HeaderTypes.PREPROCESS_LINE; }
<YYINITIAL> "__FILE__"  { return HeaderTypes.PREPROCESS_FILE; }
*/
<YYINITIAL> {IDENTIFIER} { return HeaderTypes.IDENTIFIER; }

//<YYINITIAL> "\\" { return HeaderTypes.BSLASH; }

<YYINITIAL> "="   { return HeaderTypes.EQ; }

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

<YYINITIAL> . { return HeaderTypes.BAD_CHARACTER; }



