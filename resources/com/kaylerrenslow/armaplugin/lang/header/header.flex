package com.kaylerrenslow.armaplugin.lang.header;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.armaplugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.armaplugin.lang.header.psi.HeaderParserDefinition;

%%

%public %class HeaderLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}

//the ## will be handled later with psi
// (i.e. NAME##thing: getText() will return NAME concatenated with thing and the ## will be removed)
IDENTIFIER = {LETTER} {LETTER_OR_DIGIT}*
LETTER = [:jletter:] | "$" | "##" //## is for preprocessor
LETTER_OR_DIGIT = [:jletterdigit:] | "$" | "##"

LINE_TERMINATOR = \r | \n | \r\n
INPUT_CHARACTER = [^\r\n]

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

STRING_PART = "\"" ~"\"" //if you ever decide to allow single quotes for strings, you must go back and change the search for config function tags, since tag="tag" != tag='tag'
STRING_LITERAL = {STRING_PART}+

INCLUDE_VALUE_ANGBR = "<" ([^\r\n] | \\[^\r\n])* ">"

PRE_INCLUDE = "#include"
PRE_DEFINE = "#define"
PRE_UNDEF = "#undef"
PRE_IFDEF = "#ifdef"
PRE_IFNDEF = "#ifndef"
PRE_ELSE = "#else"
PRE_ENDIF = "#endif"

PRE_DEFINE_BODY = "" // the body of a #define todo

//for preprocessor, we can try and create the element needed from the define macro's body. if can't be created, syntax error

%%

<YYINITIAL> {WHITE_SPACE} { return TokenType.WHITE_SPACE; }

<YYINITIAL> {BLOCK_COMMENT} { return HeaderParserDefinition.BLOCK_COMMENT; }
<YYINITIAL> {INLINE_COMMENT} { return HeaderParserDefinition.INLINE_COMMENT; }

<YYINITIAL> {NUMBER_LITERAL} { return HeaderTypes.NUMBER_LITERAL; }
<YYINITIAL> {HEX_LITERAL} { return HeaderTypes.HEX_LITERAL; }
<YYINITIAL> {STRING_LITERAL} { return HeaderTypes.STRING_LITERAL; }

<YYINITIAL> "class" { return HeaderTypes.CLASS; }

<YYINITIAL> {INCLUDE_VALUE_ANGBR} { return HeaderTypes.INCLUDE_VALUE_ANGBR; }
<YYINITIAL> {PRE_DEFINE_BODY}  { return HeaderTypes.PRE_DEFINE_BODY; }
<YYINITIAL> {PRE_INCLUDE}  { return HeaderTypes.PRE_INCLUDE; }
<YYINITIAL> {PRE_DEFINE}  { return HeaderTypes.PRE_DEFINE; }
<YYINITIAL> {PRE_UNDEF}  { return HeaderTypes.PRE_UNDEF; }
<YYINITIAL> {PRE_IFDEF}  { return HeaderTypes.PRE_IFDEF; }
<YYINITIAL> {PRE_IFNDEF}  { return HeaderTypes.PRE_IFNDEF; }
<YYINITIAL> {PRE_ELSE}  { return HeaderTypes.PRE_ELSE; }
<YYINITIAL> {PRE_ENDIF}  { return HeaderTypes.PRE_ENDIF; }


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
