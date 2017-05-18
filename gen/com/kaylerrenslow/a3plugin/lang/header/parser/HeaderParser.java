// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes.*;
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
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ARRAY) {
      r = array(b, 0);
    }
    else if (t == ARRAY_ASSIGNMENT) {
      r = array_assignment(b, 0);
    }
    else if (t == ARRAY_ENTRY_) {
      r = array_entry_(b, 0);
    }
    else if (t == ASSIGNMENT) {
      r = assignment(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0);
    }
    else if (t == HEADER_CLASS) {
      r = header_class(b, 0);
    }
    else if (t == PREPROCESSOR_COMMAND) {
      r = preprocessor_command(b, 0);
    }
    else if (t == PREPROCESSOR_DEFINE) {
      r = preprocessor_define(b, 0);
    }
    else if (t == PREPROCESSOR_ELSE) {
      r = preprocessor_else(b, 0);
    }
    else if (t == PREPROCESSOR_ENDIF) {
      r = preprocessor_endif(b, 0);
    }
    else if (t == PREPROCESSOR_IFDEF) {
      r = preprocessor_ifdef(b, 0);
    }
    else if (t == PREPROCESSOR_IFNDEF) {
      r = preprocessor_ifndef(b, 0);
    }
    else if (t == PREPROCESSOR_INCLUDE) {
      r = preprocessor_include(b, 0);
    }
    else if (t == PREPROCESSOR_UNDEF) {
      r = preprocessor_undef(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return headerFile(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(PREPROCESSOR_COMMAND, PREPROCESSOR_DEFINE, PREPROCESSOR_ELSE, PREPROCESSOR_ENDIF,
      PREPROCESSOR_IFDEF, PREPROCESSOR_IFNDEF, PREPROCESSOR_INCLUDE, PREPROCESSOR_UNDEF),
  };

  /* ********************************************************** */
  // mult_expression_ PLUS add_expression_
  // 				 | mult_expression_ MINUS add_expression_
  // 				 | mult_expression_
  static boolean add_expression_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_expression_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = add_expression__0(b, l + 1);
    if (!r) r = add_expression__1(b, l + 1);
    if (!r) r = mult_expression_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // mult_expression_ PLUS add_expression_
  private static boolean add_expression__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_expression__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mult_expression_(b, l + 1);
    r = r && consumeToken(b, PLUS);
    r = r && add_expression_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // mult_expression_ MINUS add_expression_
  private static boolean add_expression__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "add_expression__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mult_expression_(b, l + 1);
    r = r && consumeToken(b, MINUS);
    r = r && add_expression_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE (array_entry_ (COMMA array_entry_)*)? RBRACE
  public static boolean array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && array_1(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, ARRAY, r);
    return r;
  }

  // (array_entry_ (COMMA array_entry_)*)?
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    array_1_0(b, l + 1);
    return true;
  }

  // array_entry_ (COMMA array_entry_)*
  private static boolean array_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = array_entry_(b, l + 1);
    r = r && array_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA array_entry_)*
  private static boolean array_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!array_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_1_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA array_entry_
  private static boolean array_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && array_entry_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER BRACKET_PAIR (EQ | PLUS_EQ) array
  public static boolean array_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, BRACKET_PAIR);
    r = r && array_assignment_2(b, l + 1);
    r = r && array(b, l + 1);
    exit_section_(b, m, ARRAY_ASSIGNMENT, r);
    return r;
  }

  // EQ | PLUS_EQ
  private static boolean array_assignment_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_assignment_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    if (!r) r = consumeToken(b, PLUS_EQ);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // array | expression
  public static boolean array_entry_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_entry_")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_ENTRY_, "<array entry>");
    r = array(b, l + 1);
    if (!r) r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER EQ expression
  public static boolean assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, EQ);
    r = r && expression(b, l + 1);
    exit_section_(b, m, ASSIGNMENT, r);
    return r;
  }

  /* ********************************************************** */
  // preprocessor_command | ((assignment | array_assignment | header_class) SEMICOLON)
  static boolean entry_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = preprocessor_command(b, l + 1);
    if (!r) r = entry__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (assignment | array_assignment | header_class) SEMICOLON
  private static boolean entry__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entry__1_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // assignment | array_assignment | header_class
  private static boolean entry__1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry__1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assignment(b, l + 1);
    if (!r) r = array_assignment(b, l + 1);
    if (!r) r = header_class(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // add_expression_
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<expression>");
    r = add_expression_(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // entry_*
  static boolean headerFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headerFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!entry_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "headerFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // CLASS IDENTIFIER (COLON IDENTIFIER)? LBRACE (entry_)* RBRACE
  public static boolean header_class(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_class")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CLASS, IDENTIFIER);
    r = r && header_class_2(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && header_class_4(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, HEADER_CLASS, r);
    return r;
  }

  // (COLON IDENTIFIER)?
  private static boolean header_class_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_class_2")) return false;
    header_class_2_0(b, l + 1);
    return true;
  }

  // COLON IDENTIFIER
  private static boolean header_class_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_class_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COLON, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // (entry_)*
  private static boolean header_class_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_class_4")) return false;
    int c = current_position_(b);
    while (true) {
      if (!header_class_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "header_class_4", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (entry_)
  private static boolean header_class_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_class_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entry_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // term_ ASTERISK mult_expression_
  // 				  | term_ FSLASH mult_expression_
  // 				  | term_
  static boolean mult_expression_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mult_expression_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = mult_expression__0(b, l + 1);
    if (!r) r = mult_expression__1(b, l + 1);
    if (!r) r = term_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // term_ ASTERISK mult_expression_
  private static boolean mult_expression__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mult_expression__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term_(b, l + 1);
    r = r && consumeToken(b, ASTERISK);
    r = r && mult_expression_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // term_ FSLASH mult_expression_
  private static boolean mult_expression__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mult_expression__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term_(b, l + 1);
    r = r && consumeToken(b, FSLASH);
    r = r && mult_expression_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // preprocessor_include
  //                        | preprocessor_define
  //                        | preprocessor_undef
  //                        | preprocessor_ifdef
  //                        | preprocessor_ifndef
  //                        | preprocessor_else
  //                        | preprocessor_endif
  public static boolean preprocessor_command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_command")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, PREPROCESSOR_COMMAND, "<preprocessor command>");
    r = preprocessor_include(b, l + 1);
    if (!r) r = preprocessor_define(b, l + 1);
    if (!r) r = preprocessor_undef(b, l + 1);
    if (!r) r = preprocessor_ifdef(b, l + 1);
    if (!r) r = preprocessor_ifndef(b, l + 1);
    if (!r) r = preprocessor_else(b, l + 1);
    if (!r) r = preprocessor_endif(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PRE_DEFINE IDENTIFIER PRE_DEFINE_BODY
  public static boolean preprocessor_define(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_define")) return false;
    if (!nextTokenIs(b, PRE_DEFINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PRE_DEFINE, IDENTIFIER, PRE_DEFINE_BODY);
    exit_section_(b, m, PREPROCESSOR_DEFINE, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_ELSE
  public static boolean preprocessor_else(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_else")) return false;
    if (!nextTokenIs(b, PRE_ELSE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PRE_ELSE);
    exit_section_(b, m, PREPROCESSOR_ELSE, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_ENDIF
  public static boolean preprocessor_endif(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_endif")) return false;
    if (!nextTokenIs(b, PRE_ENDIF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PRE_ENDIF);
    exit_section_(b, m, PREPROCESSOR_ENDIF, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_IFDEF IDENTIFIER
  public static boolean preprocessor_ifdef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_ifdef")) return false;
    if (!nextTokenIs(b, PRE_IFDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PRE_IFDEF, IDENTIFIER);
    exit_section_(b, m, PREPROCESSOR_IFDEF, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_IFNDEF IDENTIFIER
  public static boolean preprocessor_ifndef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_ifndef")) return false;
    if (!nextTokenIs(b, PRE_IFNDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PRE_IFNDEF, IDENTIFIER);
    exit_section_(b, m, PREPROCESSOR_IFNDEF, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_INCLUDE (STRING_LITERAL | INCLUDE_VALUE_ANGBR)
  public static boolean preprocessor_include(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_include")) return false;
    if (!nextTokenIs(b, PRE_INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PRE_INCLUDE);
    r = r && preprocessor_include_1(b, l + 1);
    exit_section_(b, m, PREPROCESSOR_INCLUDE, r);
    return r;
  }

  // STRING_LITERAL | INCLUDE_VALUE_ANGBR
  private static boolean preprocessor_include_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_include_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING_LITERAL);
    if (!r) r = consumeToken(b, INCLUDE_VALUE_ANGBR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PRE_UNDEF IDENTIFIER
  public static boolean preprocessor_undef(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_undef")) return false;
    if (!nextTokenIs(b, PRE_UNDEF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PRE_UNDEF, IDENTIFIER);
    exit_section_(b, m, PREPROCESSOR_UNDEF, r);
    return r;
  }

  /* ********************************************************** */
  // (PLUS | MINUS)?
  //                 (
  //                     NUMBER_LITERAL
  //                     | HEX_LITERAL
  //                     | IDENTIFIER
  //                     | STRING_LITERAL
  //                     | LPAREN expression RPAREN
  //                 )
  static boolean term_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term__0(b, l + 1);
    r = r && term__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PLUS | MINUS)?
  private static boolean term__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__0")) return false;
    term__0_0(b, l + 1);
    return true;
  }

  // PLUS | MINUS
  private static boolean term__0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER_LITERAL
  //                     | HEX_LITERAL
  //                     | IDENTIFIER
  //                     | STRING_LITERAL
  //                     | LPAREN expression RPAREN
  private static boolean term__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NUMBER_LITERAL);
    if (!r) r = consumeToken(b, HEX_LITERAL);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, STRING_LITERAL);
    if (!r) r = term__1_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expression RPAREN
  private static boolean term__1_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__1_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

}
