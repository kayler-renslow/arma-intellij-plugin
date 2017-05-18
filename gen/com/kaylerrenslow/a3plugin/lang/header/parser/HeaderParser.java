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
    else if (t == ARRAY_BODY) {
      r = array_body(b, 0);
    }
    else if (t == ARRAY_ENTRY) {
      r = array_entry(b, 0);
    }
    else if (t == ASSIGNMENT) {
      r = assignment(b, 0);
    }
    else if (t == ASSIGNMENT_IDENTIFIER) {
      r = assignment_identifier(b, 0);
    }
    else if (t == BASIC_ASSIGNMENT) {
      r = basic_assignment(b, 0);
    }
    else if (t == CLASS_CONTENT) {
      r = class_content(b, 0);
    }
    else if (t == CLASS_DECLARATION) {
      r = class_declaration(b, 0);
    }
    else if (t == CLASS_STUB) {
      r = class_stub(b, 0);
    }
    else if (t == DEFINED_TEXT) {
      r = defined_text(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0);
    }
    else if (t == FILE_ENTRIES) {
      r = file_entries(b, 0);
    }
    else if (t == FILE_ENTRY) {
      r = file_entry(b, 0);
    }
    else if (t == MACRO_FUNCTION) {
      r = macro_function(b, 0);
    }
    else if (t == PRE_EVAL) {
      r = pre_eval(b, 0);
    }
    else if (t == PRE_EXEC) {
      r = pre_exec(b, 0);
    }
    else if (t == PRE_INCLUDE) {
      r = pre_include(b, 0);
    }
    else if (t == PRE_INCLUDE_FILE) {
      r = pre_include_file(b, 0);
    }
    else if (t == PRE_MACRO) {
      r = pre_macro(b, 0);
    }
    else if (t == PREPROCESSOR) {
      r = preprocessor(b, 0);
    }
    else if (t == PREPROCESSOR_GROUP) {
      r = preprocessor_group(b, 0);
    }
    else if (t == STRINGTABLE_KEY) {
      r = stringtable_key(b, 0);
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
    create_token_set_(PREPROCESSOR, PRE_INCLUDE),
    create_token_set_(ARRAY_ASSIGNMENT, ASSIGNMENT, BASIC_ASSIGNMENT),
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
  // LBRACE array_body? RBRACE
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

  // array_body?
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    array_body(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // assignment_identifier BRACKET_PAIR (EQ | PLUS_EQ) array
  public static boolean array_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assignment_identifier(b, l + 1);
    r = r && consumeToken(b, BRACKET_PAIR);
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
  // array_entry (COMMA array_entry)*
  public static boolean array_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_BODY, "<array body>");
    r = array_entry(b, l + 1);
    r = r && array_body_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA array_entry)*
  private static boolean array_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_body_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!array_body_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_body_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA array_entry
  private static boolean array_body_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_body_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && array_entry(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression | value_ | array | stringtable_key
  public static boolean array_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_entry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_ENTRY, "<array entry>");
    r = expression(b, l + 1);
    if (!r) r = value_(b, l + 1);
    if (!r) r = array(b, l + 1);
    if (!r) r = stringtable_key(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // basic_assignment | array_assignment
  public static boolean assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ASSIGNMENT, null);
    r = basic_assignment(b, l + 1);
    if (!r) r = array_assignment(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean assignment_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_identifier")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, ASSIGNMENT_IDENTIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // assignment_identifier EQ (stringtable_key | expression | value_)
  public static boolean basic_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "basic_assignment")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assignment_identifier(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && basic_assignment_2(b, l + 1);
    exit_section_(b, m, BASIC_ASSIGNMENT, r);
    return r;
  }

  // stringtable_key | expression | value_
  private static boolean basic_assignment_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "basic_assignment_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = stringtable_key(b, l + 1);
    if (!r) r = expression(b, l + 1);
    if (!r) r = value_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE file_entries RBRACE
  public static boolean class_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_content")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && file_entries(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, CLASS_CONTENT, r);
    return r;
  }

  /* ********************************************************** */
  // class_stub class_content?
  public static boolean class_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_declaration")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = class_stub(b, l + 1);
    r = r && class_declaration_1(b, l + 1);
    exit_section_(b, m, CLASS_DECLARATION, r);
    return r;
  }

  // class_content?
  private static boolean class_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_declaration_1")) return false;
    class_content(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CLASS IDENTIFIER (COLON IDENTIFIER)?
  public static boolean class_stub(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_stub")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CLASS, IDENTIFIER);
    r = r && class_stub_2(b, l + 1);
    exit_section_(b, m, CLASS_STUB, r);
    return r;
  }

  // (COLON IDENTIFIER)?
  private static boolean class_stub_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_stub_2")) return false;
    class_stub_2_0(b, l + 1);
    return true;
  }

  // COLON IDENTIFIER
  private static boolean class_stub_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_stub_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COLON, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean defined_text(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defined_text")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, DEFINED_TEXT, r);
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
  // file_entry*
  public static boolean file_entries(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_entries")) return false;
    Marker m = enter_section_(b, l, _NONE_, FILE_ENTRIES, "<file entries>");
    int c = current_position_(b);
    while (true) {
      if (!file_entry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file_entries", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // INLINE_COMMENT | BLOCK_COMMENT | preprocessor_group | statement_
  public static boolean file_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_entry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_ENTRY, "<file entry>");
    r = consumeToken(b, INLINE_COMMENT);
    if (!r) r = consumeToken(b, BLOCK_COMMENT);
    if (!r) r = preprocessor_group(b, l + 1);
    if (!r) r = statement_(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // file_entry*
  static boolean headerFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headerFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!file_entry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "headerFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER LPAREN ((value_ | array) (COMMA (value_ | array))*) RPAREN | pre_eval
  public static boolean macro_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function")) return false;
    if (!nextTokenIs(b, "<macro function>", IDENTIFIER, PREPROCESS_EVAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_FUNCTION, "<macro function>");
    r = macro_function_0(b, l + 1);
    if (!r) r = pre_eval(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IDENTIFIER LPAREN ((value_ | array) (COMMA (value_ | array))*) RPAREN
  private static boolean macro_function_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, LPAREN);
    r = r && macro_function_0_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (value_ | array) (COMMA (value_ | array))*
  private static boolean macro_function_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = macro_function_0_2_0(b, l + 1);
    r = r && macro_function_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // value_ | array
  private static boolean macro_function_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_(b, l + 1);
    if (!r) r = array(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA (value_ | array))*
  private static boolean macro_function_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0_2_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!macro_function_0_2_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_function_0_2_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA (value_ | array)
  private static boolean macro_function_0_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && macro_function_0_2_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // value_ | array
  private static boolean macro_function_0_2_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_function_0_2_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_(b, l + 1);
    if (!r) r = array(b, l + 1);
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
  // NUMBER_LITERAL | HEX_LITERAL
  static boolean number_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number_")) return false;
    if (!nextTokenIs(b, "", HEX_LITERAL, NUMBER_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NUMBER_LITERAL);
    if (!r) r = consumeToken(b, HEX_LITERAL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PREPROCESS_EVAL
  public static boolean pre_eval(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pre_eval")) return false;
    if (!nextTokenIs(b, PREPROCESS_EVAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PREPROCESS_EVAL);
    exit_section_(b, m, PRE_EVAL, r);
    return r;
  }

  /* ********************************************************** */
  // PREPROCESS_EXEC
  public static boolean pre_exec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pre_exec")) return false;
    if (!nextTokenIs(b, PREPROCESS_EXEC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PREPROCESS_EXEC);
    exit_section_(b, m, PRE_EXEC, r);
    return r;
  }

  /* ********************************************************** */
  // PREPROCESS_INCLUDE pre_include_file
  public static boolean pre_include(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pre_include")) return false;
    if (!nextTokenIs(b, PREPROCESS_INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PREPROCESS_INCLUDE);
    r = r && pre_include_file(b, l + 1);
    exit_section_(b, m, PRE_INCLUDE, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_LITERAL | INCLUDE_VALUE_ANGBR
  public static boolean pre_include_file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pre_include_file")) return false;
    if (!nextTokenIs(b, "<pre include file>", INCLUDE_VALUE_ANGBR, STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRE_INCLUDE_FILE, "<pre include file>");
    r = consumeToken(b, STRING_LITERAL);
    if (!r) r = consumeToken(b, INCLUDE_VALUE_ANGBR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PREPROCESS_MACRO
  public static boolean pre_macro(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pre_macro")) return false;
    if (!nextTokenIs(b, PREPROCESS_MACRO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PREPROCESS_MACRO);
    exit_section_(b, m, PRE_MACRO, r);
    return r;
  }

  /* ********************************************************** */
  // pre_include | pre_macro | pre_exec
  public static boolean preprocessor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, PREPROCESSOR, "<preprocessor>");
    r = pre_include(b, l + 1);
    if (!r) r = pre_macro(b, l + 1);
    if (!r) r = pre_exec(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // preprocessor+
  public static boolean preprocessor_group(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "preprocessor_group")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PREPROCESSOR_GROUP, "<preprocessor group>");
    r = preprocessor(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!preprocessor(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "preprocessor_group", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (assignment | class_declaration) SEMICOLON | macro_function SEMICOLON? | defined_text SEMICOLON?
  static boolean statement_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = statement__0(b, l + 1);
    if (!r) r = statement__1(b, l + 1);
    if (!r) r = statement__2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (assignment | class_declaration) SEMICOLON
  private static boolean statement__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = statement__0_0(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // assignment | class_declaration
  private static boolean statement__0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assignment(b, l + 1);
    if (!r) r = class_declaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // macro_function SEMICOLON?
  private static boolean statement__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = macro_function(b, l + 1);
    r = r && statement__1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SEMICOLON?
  private static boolean statement__1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__1_1")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  // defined_text SEMICOLON?
  private static boolean statement__2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = defined_text(b, l + 1);
    r = r && statement__2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SEMICOLON?
  private static boolean statement__2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement__2_1")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // STRINGTABLE_ENTRY
  public static boolean stringtable_key(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringtable_key")) return false;
    if (!nextTokenIs(b, STRINGTABLE_ENTRY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRINGTABLE_ENTRY);
    exit_section_(b, m, STRINGTABLE_KEY, r);
    return r;
  }

  /* ********************************************************** */
  // (PLUS | MINUS)? (
  // 			number_
  //             | IDENTIFIER
  // 			| LPAREN expression RPAREN
  // 		  )
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

  // number_
  //             | IDENTIFIER
  // 			| LPAREN expression RPAREN
  private static boolean term__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = number_(b, l + 1);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = term__1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expression RPAREN
  private static boolean term__1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term__1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STRING_LITERAL | number_ | IDENTIFIER | macro_function
  static boolean value_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING_LITERAL);
    if (!r) r = number_(b, l + 1);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = macro_function(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
