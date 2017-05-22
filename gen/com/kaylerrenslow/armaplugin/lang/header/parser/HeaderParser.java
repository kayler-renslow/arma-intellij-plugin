// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.header.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.kaylerrenslow.armaplugin.lang.header.psi.HeaderTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class HeaderParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b, 0);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return headerFile(b, l + 1);
  }

  /* ********************************************************** */
  // token_*
  static boolean headerFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headerFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!token_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "headerFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (/*numbers and string*/ NUMBER_LITERAL | HEX_LITERAL | STRING_LITERAL)
  //                  | (/*keywords*/ CLASS)
  //                  | (/*operators*/ EQ | PLUS_EQ | PLUS | MINUS | FSLASH | ASTERISK)
  //                  | (/*symbols*/  LBRACE | RBRACE | LPAREN | RPAREN | BRACKET_PAIR | COMMA | COLON | SEMICOLON)
  //                  | (/*preprocessor*/ MACRO)
  //                  | (/*variables*/ IDENTIFIER)
  static boolean token_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "token_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = token__0(b, l + 1);
    if (!r) r = consumeToken(b, CLASS);
    if (!r) r = token__2(b, l + 1);
    if (!r) r = token__3(b, l + 1);
    if (!r) r = consumeToken(b, MACRO);
    if (!r) r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_LITERAL | HEX_LITERAL | STRING_LITERAL
  private static boolean token__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "token__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NUMBER_LITERAL);
    if (!r) r = consumeToken(b, HEX_LITERAL);
    if (!r) r = consumeToken(b, STRING_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ | PLUS_EQ | PLUS | MINUS | FSLASH | ASTERISK
  private static boolean token__2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "token__2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, PLUS_EQ);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, FSLASH);
    if (!r) r = consumeToken(b, ASTERISK);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE | RBRACE | LPAREN | RPAREN | BRACKET_PAIR | COMMA | COLON | SEMICOLON
  private static boolean token__3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "token__3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, LPAREN);
    if (!r) r = consumeToken(b, RPAREN);
    if (!r) r = consumeToken(b, BRACKET_PAIR);
    if (!r) r = consumeToken(b, COMMA);
    if (!r) r = consumeToken(b, COLON);
    if (!r) r = consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

}
