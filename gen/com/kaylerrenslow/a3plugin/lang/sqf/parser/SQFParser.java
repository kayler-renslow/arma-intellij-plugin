// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SQFParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ARRAY_ENTRY) {
      r = array_entry(b, 0);
    }
    else if (t == ARRAY_VAL) {
      r = array_val(b, 0);
    }
    else if (t == ASSIGNMENT) {
      r = assignment(b, 0);
    }
    else if (t == CASE_COMMAND) {
      r = case_command(b, 0);
    }
    else if (t == CASE_STATEMENT) {
      r = case_statement(b, 0);
    }
    else if (t == COMMAND) {
      r = command(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0, -1);
    }
    else if (t == FILE_SCOPE) {
      r = file_scope(b, 0);
    }
    else if (t == LOCAL_SCOPE) {
      r = local_scope(b, 0);
    }
    else if (t == MACRO_CALL) {
      r = macro_call(b, 0);
    }
    else if (t == QUEST_STATEMENT) {
      r = quest_statement(b, 0);
    }
    else if (t == STATEMENT) {
      r = statement(b, 0);
    }
    else if (t == STRING) {
      r = string(b, 0);
    }
    else if (t == VARIABLE) {
      r = variable(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return sqfFile(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(FILE_SCOPE, LOCAL_SCOPE),
    create_token_set_(CASE_COMMAND, COMMAND),
    create_token_set_(CASE_STATEMENT, QUEST_STATEMENT, STATEMENT),
    create_token_set_(ADD_EXPRESSION, BOOL_AND_EXPRESSION, BOOL_NOT_EXPRESSION, BOOL_OR_EXPRESSION,
      CODE_BLOCK, COMMAND_EXPRESSION, COMP_EXPRESSION, CONFIG_FETCH_EXPRESSION,
      DIV_EXPRESSION, EXPONENT_EXPRESSION, EXPRESSION, LITERAL_EXPRESSION,
      MOD_EXPRESSION, MUL_EXPRESSION, PAREN_EXPRESSION, SUB_EXPRESSION,
      UNARY_EXPRESSION),
  };

  /* ********************************************************** */
  // expression
  public static boolean array_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_entry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_ENTRY, "<array entry>");
    r = expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (LBRACKET RBRACKET) | (LBRACKET array_entry (COMMA array_entry)* RBRACKET )
  public static boolean array_val(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_val")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = array_val_0(b, l + 1);
    if (!r) r = array_val_1(b, l + 1);
    exit_section_(b, m, ARRAY_VAL, r);
    return r;
  }

  // LBRACKET RBRACKET
  private static boolean array_val_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_val_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACKET array_entry (COMMA array_entry)* RBRACKET
  private static boolean array_val_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_val_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && array_entry(b, l + 1);
    r = r && array_val_1_2(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA array_entry)*
  private static boolean array_val_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_val_1_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!array_val_1_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_val_1_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA array_entry
  private static boolean array_val_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_val_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && array_entry(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (command? variable) EQ expression
  public static boolean assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT, "<Assignment>");
    r = assignment_0(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // command? variable
  private static boolean assignment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = assignment_0_0(b, l + 1);
    r = r && variable(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // command?
  private static boolean assignment_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_0_0")) return false;
    command(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CASE
  public static boolean case_command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_command")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    exit_section_(b, m, CASE_COMMAND, r);
    return r;
  }

  /* ********************************************************** */
  // case_command expression (COLON code_block)?
  public static boolean case_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_statement")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = case_command(b, l + 1);
    r = r && expression(b, l + 1, -1);
    r = r && case_statement_2(b, l + 1);
    exit_section_(b, m, CASE_STATEMENT, r);
    return r;
  }

  // (COLON code_block)?
  private static boolean case_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_statement_2")) return false;
    case_statement_2_0(b, l + 1);
    return true;
  }

  // COLON code_block
  private static boolean case_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && code_block(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COMMAND_TOKEN
  public static boolean command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command")) return false;
    if (!nextTokenIs(b, COMMAND_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_TOKEN);
    exit_section_(b, m, COMMAND, r);
    return r;
  }

  /* ********************************************************** */
  // (code_block expression?) | expression
  static boolean command_postfix_arg_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_postfix_arg_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = command_postfix_arg__0(b, l + 1);
    if (!r) r = expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // code_block expression?
  private static boolean command_postfix_arg__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_postfix_arg__0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = code_block(b, l + 1);
    r = r && command_postfix_arg__0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression?
  private static boolean command_postfix_arg__0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_postfix_arg__0_1")) return false;
    expression(b, l + 1, -1);
    return true;
  }

  /* ********************************************************** */
  // code_block | unary_expression | paren_expression | literal_expression
  static boolean command_prefix_arg_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_prefix_arg_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = code_block(b, l + 1);
    if (!r) r = unary_expression(b, l + 1);
    if (!r) r = paren_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // items_
  public static boolean file_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_scope")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_SCOPE, "<file scope>");
    r = items_(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (statement SEMICOLON)* statement?
  static boolean items_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "items_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = items__0(b, l + 1);
    r = r && items__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (statement SEMICOLON)*
  private static boolean items__0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "items__0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!items__0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "items__0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // statement SEMICOLON
  private static boolean items__0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "items__0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = statement(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // statement?
  private static boolean items__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "items__1")) return false;
    statement(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // items_
  public static boolean local_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_scope")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_SCOPE, "<local scope>");
    r = items_(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // variable LPAREN expression (COMMA expression)* RPAREN
  public static boolean macro_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_call")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO_CALL, "<Macro Call>");
    r = variable(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && expression(b, l + 1, -1);
    r = r && macro_call_3(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA expression)*
  private static boolean macro_call_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_call_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!macro_call_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_call_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA expression
  private static boolean macro_call_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_call_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // QUEST expression COLON expression
  public static boolean quest_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quest_statement")) return false;
    if (!nextTokenIs(b, QUEST)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUEST);
    r = r && expression(b, l + 1, -1);
    r = r && consumeToken(b, COLON);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, QUEST_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // file_scope
  static boolean sqfFile(PsiBuilder b, int l) {
    return file_scope(b, l + 1);
  }

  /* ********************************************************** */
  // assignment | case_statement | expression | quest_statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STATEMENT, "<statement>");
    r = assignment(b, l + 1);
    if (!r) r = case_statement(b, l + 1);
    if (!r) r = expression(b, l + 1, -1);
    if (!r) r = quest_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRING_LITERAL
  public static boolean string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string")) return false;
    if (!nextTokenIs(b, STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING_LITERAL);
    exit_section_(b, m, STRING, r);
    return r;
  }

  /* ********************************************************** */
  // GLOBAL_VAR | LOCAL_VAR | LANG_VAR
  public static boolean variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE, "<variable>");
    r = consumeToken(b, GLOBAL_VAR);
    if (!r) r = consumeToken(b, LOCAL_VAR);
    if (!r) r = consumeToken(b, LANG_VAR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: BINARY(bool_and_expression) BINARY(bool_or_expression) PREFIX(bool_not_expression)
  // 1: BINARY(add_expression) BINARY(sub_expression)
  // 2: BINARY(comp_expression)
  // 3: BINARY(mul_expression) BINARY(div_expression) BINARY(mod_expression)
  // 4: N_ARY(exponent_expression)
  // 5: BINARY(config_fetch_expression)
  // 6: ATOM(command_expression)
  // 7: ATOM(unary_expression)
  // 8: ATOM(literal_expression)
  // 9: ATOM(code_block)
  // 10: PREFIX(paren_expression)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<Expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<Expression>");
    r = bool_not_expression(b, l + 1);
    if (!r) r = command_expression(b, l + 1);
    if (!r) r = unary_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    if (!r) r = code_block(b, l + 1);
    if (!r) r = paren_expression(b, l + 1);
    p = r;
    r = r && expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && consumeTokenSmart(b, AMPAMP)) {
        r = expression(b, l, 0);
        exit_section_(b, l, m, BOOL_AND_EXPRESSION, r, true, null);
      }
      else if (g < 0 && consumeTokenSmart(b, BARBAR)) {
        r = expression(b, l, 0);
        exit_section_(b, l, m, BOOL_OR_EXPRESSION, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, PLUS)) {
        r = expression(b, l, 1);
        exit_section_(b, l, m, ADD_EXPRESSION, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, MINUS)) {
        r = expression(b, l, 1);
        exit_section_(b, l, m, SUB_EXPRESSION, r, true, null);
      }
      else if (g < 2 && comp_expression_0(b, l + 1)) {
        r = expression(b, l, 2);
        exit_section_(b, l, m, COMP_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, ASTERISK)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, MUL_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, FSLASH)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, DIV_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, PERC)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, MOD_EXPRESSION, r, true, null);
      }
      else if (g < 4 && consumeTokenSmart(b, CARET)) {
        while (true) {
          r = report_error_(b, expression(b, l, 4));
          if (!consumeTokenSmart(b, CARET)) break;
        }
        exit_section_(b, l, m, EXPONENT_EXPRESSION, r, true, null);
      }
      else if (g < 5 && config_fetch_expression_0(b, l + 1)) {
        r = expression(b, l, 5);
        exit_section_(b, l, m, CONFIG_FETCH_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean bool_not_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bool_not_expression")) return false;
    if (!nextTokenIsSmart(b, EXCL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, EXCL);
    p = r;
    r = p && expression(b, l, 0);
    exit_section_(b, l, m, BOOL_NOT_EXPRESSION, r, p, null);
    return r || p;
  }

  // EQEQ | NE | LT | LE | GT | GE
  private static boolean comp_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comp_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, EQEQ);
    if (!r) r = consumeTokenSmart(b, NE);
    if (!r) r = consumeTokenSmart(b, LT);
    if (!r) r = consumeTokenSmart(b, LE);
    if (!r) r = consumeTokenSmart(b, GT);
    if (!r) r = consumeTokenSmart(b, GE);
    exit_section_(b, m, null, r);
    return r;
  }

  // FSLASH | GTGT
  private static boolean config_fetch_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "config_fetch_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, FSLASH);
    if (!r) r = consumeTokenSmart(b, GTGT);
    exit_section_(b, m, null, r);
    return r;
  }

  // command_prefix_arg_? command command_postfix_arg_?
  public static boolean command_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, COMMAND_EXPRESSION, "<command expression>");
    r = command_expression_0(b, l + 1);
    r = r && command(b, l + 1);
    r = r && command_expression_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // command_prefix_arg_?
  private static boolean command_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_0")) return false;
    command_prefix_arg_(b, l + 1);
    return true;
  }

  // command_postfix_arg_?
  private static boolean command_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_2")) return false;
    command_postfix_arg_(b, l + 1);
    return true;
  }

  // (PLUS | MINUS) (expression)
  public static boolean unary_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_expression")) return false;
    if (!nextTokenIsSmart(b, MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, UNARY_EXPRESSION, "<unary expression>");
    r = unary_expression_0(b, l + 1);
    r = r && unary_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PLUS | MINUS
  private static boolean unary_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, PLUS);
    if (!r) r = consumeTokenSmart(b, MINUS);
    exit_section_(b, m, null, r);
    return r;
  }

  // (expression)
  private static boolean unary_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_expression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  // string
  //                     | macro_call //must come before variable
  //                     | variable
  //                     | INTEGER_LITERAL
  //                     | array_val
  //                     | DEC_LITERAL
  //                     | HEX_LITERAL
  public static boolean literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_EXPRESSION, "<literal expression>");
    r = string(b, l + 1);
    if (!r) r = macro_call(b, l + 1);
    if (!r) r = variable(b, l + 1);
    if (!r) r = consumeTokenSmart(b, INTEGER_LITERAL);
    if (!r) r = array_val(b, l + 1);
    if (!r) r = consumeTokenSmart(b, DEC_LITERAL);
    if (!r) r = consumeTokenSmart(b, HEX_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LBRACE RBRACE)| (LBRACE local_scope RBRACE)
  public static boolean code_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block")) return false;
    if (!nextTokenIsSmart(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = code_block_0(b, l + 1);
    if (!r) r = code_block_1(b, l + 1);
    exit_section_(b, m, CODE_BLOCK, r);
    return r;
  }

  // LBRACE RBRACE
  private static boolean code_block_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, LBRACE, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE local_scope RBRACE
  private static boolean code_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LBRACE);
    r = r && local_scope(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  public static boolean paren_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expression")) return false;
    if (!nextTokenIsSmart(b, LPAREN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, LPAREN);
    p = r;
    r = p && expression(b, l, -1);
    r = p && report_error_(b, consumeToken(b, RPAREN)) && r;
    exit_section_(b, l, m, PAREN_EXPRESSION, r, p, null);
    return r || p;
  }

}
