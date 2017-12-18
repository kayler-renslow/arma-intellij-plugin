// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.*;
import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFParserUtil.*;
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
    if (t == ARRAY) {
      r = array(b, 0);
    }
    else if (t == ASSIGNMENT_STATEMENT) {
      r = assignment_statement(b, 0);
    }
    else if (t == CASE_COMMAND) {
      r = case_command(b, 0);
    }
    else if (t == CASE_STATEMENT) {
      r = case_statement(b, 0);
    }
    else if (t == CODE_BLOCK) {
      r = code_block(b, 0);
    }
    else if (t == CODE_BLOCK_EXPRESSION) {
      r = code_block_expression(b, 0);
    }
    else if (t == COMMAND) {
      r = command(b, 0);
    }
    else if (t == COMMAND_AFTER) {
      r = command_after(b, 0);
    }
    else if (t == COMMAND_BEFORE) {
      r = command_before(b, 0);
    }
    else if (t == COMMAND_EXPRESSION) {
      r = command_expression(b, 0);
    }
    else if (t == EXPRESSION) {
      r = expression(b, 0);
    }
    else if (t == EXPRESSION_OPERATOR) {
      r = expression_operator(b, 0);
    }
    else if (t == EXPRESSION_STATEMENT) {
      r = expression_statement(b, 0);
    }
    else if (t == FILE_SCOPE) {
      r = file_scope(b, 0);
    }
    else if (t == LITERAL_EXPRESSION) {
      r = literal_expression(b, 0);
    }
    else if (t == LOCAL_SCOPE) {
      r = local_scope(b, 0);
    }
    else if (t == MACRO_CALL) {
      r = macro_call(b, 0);
    }
    else if (t == NUMBER) {
      r = number(b, 0);
    }
    else if (t == PAREN_EXPRESSION) {
      r = paren_expression(b, 0);
    }
    else if (t == PLUS_OR_MINUS_EXPRESION_OPERATOR) {
      r = plus_or_minus_expresion_operator(b, 0);
    }
    else if (t == PRIVATE_COMMAND) {
      r = private_command(b, 0);
    }
    else if (t == QUEST_STATEMENT) {
      r = quest_statement(b, 0);
    }
    else if (t == SIGNED_EXPRESSION) {
      r = signed_expression(b, 0);
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
    create_token_set_(CASE_COMMAND, COMMAND, PRIVATE_COMMAND),
    create_token_set_(ASSIGNMENT_STATEMENT, CASE_STATEMENT, EXPRESSION_STATEMENT, QUEST_STATEMENT,
      STATEMENT),
    create_token_set_(CODE_BLOCK_EXPRESSION, COMMAND_EXPRESSION, EXPRESSION, LITERAL_EXPRESSION,
      PAREN_EXPRESSION, SIGNED_EXPRESSION),
  };

  /* ********************************************************** */
  // L_SQ_BRACKET (expression (COMMA expression)*)? R_SQ_BRACKET
  public static boolean array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array")) return false;
    if (!nextTokenIs(b, L_SQ_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ARRAY, null);
    r = consumeToken(b, L_SQ_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, array_1(b, l + 1));
    r = p && consumeToken(b, R_SQ_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (expression (COMMA expression)*)?
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    array_1_0(b, l + 1);
    return true;
  }

  // expression (COMMA expression)*
  private static boolean array_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && array_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression)*
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

  // COMMA expression
  private static boolean array_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // private_command? variable
  //                         !(command /*make sure not to expect part of a command expression*/)
  //                         EQ expression
  public static boolean assignment_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_STATEMENT, "<assignment statement>");
    r = assignment_statement_0(b, l + 1);
    r = r && variable(b, l + 1);
    r = r && assignment_statement_2(b, l + 1);
    r = r && consumeToken(b, EQ);
    p = r; // pin = 4
    r = r && expression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // private_command?
  private static boolean assignment_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_statement_0")) return false;
    private_command(b, l + 1);
    return true;
  }

  // !(command /*make sure not to expect part of a command expression*/)
  private static boolean assignment_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_statement_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !assignment_statement_2_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (command /*make sure not to expect part of a command expression*/)
  private static boolean assignment_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = command(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<external_rule_case_command 0>>
  public static boolean case_command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_command")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, CASE_COMMAND, "<case command>");
    r = external_rule_case_command(b, l + 1, 0);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // case_command expression (COLON code_block)?
  public static boolean case_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "case_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CASE_STATEMENT, "<case statement>");
    r = case_command(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1));
    r = p && case_statement_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // L_CURLY_BRACE local_scope? R_CURLY_BRACE
  public static boolean code_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block")) return false;
    if (!nextTokenIs(b, L_CURLY_BRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY_BRACE);
    r = r && code_block_1(b, l + 1);
    r = r && consumeToken(b, R_CURLY_BRACE);
    exit_section_(b, m, CODE_BLOCK, r);
    return r;
  }

  // local_scope?
  private static boolean code_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block_1")) return false;
    local_scope(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // code_block
  public static boolean code_block_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_block_expression")) return false;
    if (!nextTokenIs(b, L_CURLY_BRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = code_block(b, l + 1);
    exit_section_(b, m, CODE_BLOCK_EXPRESSION, r);
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
  // expression
  public static boolean command_after(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_after")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_AFTER, "<command after>");
    r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // code_block_expression | paren_expression | signed_expression | literal_expression
  public static boolean command_before(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_before")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_BEFORE, "<command before>");
    r = code_block_expression(b, l + 1);
    if (!r) r = paren_expression(b, l + 1);
    if (!r) r = signed_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // command_before? expression_operator command_after?
  public static boolean command_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_EXPRESSION, "<command expression>");
    r = command_expression_0(b, l + 1);
    r = r && expression_operator(b, l + 1);
    p = r; // pin = 2
    r = r && command_expression_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // command_before?
  private static boolean command_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_0")) return false;
    command_before(b, l + 1);
    return true;
  }

  // command_after?
  private static boolean command_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_2")) return false;
    command_after(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // command_expression
  //                 | signed_expression //we don't need signed_expression here because it is handled by command_expression
  //                 | literal_expression
  //                 | code_block_expression
  //                 | paren_expression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<expression>");
    r = command_expression(b, l + 1);
    if (!r) r = signed_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    if (!r) r = code_block_expression(b, l + 1);
    if (!r) r = paren_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // command
  //                             | PLUS| MINUS | ASTERISK | FSLASH | PERC | CARET
  //                             | AMPAMP | BARBAR | EXCL
  //                             | EQEQ | NE | LT | LE | GT | GE
  //                             | GTGT
  public static boolean expression_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_OPERATOR, "<expression operator>");
    r = command(b, l + 1);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, ASTERISK);
    if (!r) r = consumeToken(b, FSLASH);
    if (!r) r = consumeToken(b, PERC);
    if (!r) r = consumeToken(b, CARET);
    if (!r) r = consumeToken(b, AMPAMP);
    if (!r) r = consumeToken(b, BARBAR);
    if (!r) r = consumeToken(b, EXCL);
    if (!r) r = consumeToken(b, EQEQ);
    if (!r) r = consumeToken(b, NE);
    if (!r) r = consumeToken(b, LT);
    if (!r) r = consumeToken(b, LE);
    if (!r) r = consumeToken(b, GT);
    if (!r) r = consumeToken(b, GE);
    if (!r) r = consumeToken(b, GTGT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expression
  public static boolean expression_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_STATEMENT, "<expression statement>");
    r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // string
  //                         | variable
  //                         | array
  //                         | number
  //                         | macro_call
  public static boolean literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_EXPRESSION, "<literal expression>");
    r = string(b, l + 1);
    if (!r) r = variable(b, l + 1);
    if (!r) r = array(b, l + 1);
    if (!r) r = number(b, l + 1);
    if (!r) r = macro_call(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
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
  // MACRO_FUNC
  public static boolean macro_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_call")) return false;
    if (!nextTokenIs(b, MACRO_FUNC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MACRO_FUNC);
    exit_section_(b, m, MACRO_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // INTEGER_LITERAL | DEC_LITERAL | HEX_LITERAL
  public static boolean number(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NUMBER, "<number>");
    r = consumeToken(b, INTEGER_LITERAL);
    if (!r) r = consumeToken(b, DEC_LITERAL);
    if (!r) r = consumeToken(b, HEX_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN expression RPAREN
  public static boolean paren_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expression")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, PAREN_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // PLUS | MINUS
  public static boolean plus_or_minus_expresion_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "plus_or_minus_expresion_operator")) return false;
    if (!nextTokenIs(b, "<plus or minus expresion operator>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PLUS_OR_MINUS_EXPRESION_OPERATOR, "<plus or minus expresion operator>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // <<external_rule_private_command 0>>
  public static boolean private_command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "private_command")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, PRIVATE_COMMAND, "<private command>");
    r = external_rule_private_command(b, l + 1, 0);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // QUEST expression COLON expression
  public static boolean quest_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quest_statement")) return false;
    if (!nextTokenIs(b, QUEST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, QUEST_STATEMENT, null);
    r = consumeToken(b, QUEST);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1));
    r = p && report_error_(b, consumeToken(b, COLON)) && r;
    r = p && expression(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // plus_or_minus_expresion_operator command_after
  public static boolean signed_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "signed_expression")) return false;
    if (!nextTokenIs(b, "<signed expression>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIGNED_EXPRESSION, "<signed expression>");
    r = plus_or_minus_expresion_operator(b, l + 1);
    r = r && command_after(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // file_scope
  static boolean sqfFile(PsiBuilder b, int l) {
    return file_scope(b, l + 1);
  }

  /* ********************************************************** */
  // assignment_statement | case_statement | expression_statement | quest_statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STATEMENT, "<statement>");
    r = assignment_statement(b, l + 1);
    if (!r) r = case_statement(b, l + 1);
    if (!r) r = expression_statement(b, l + 1);
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
  // GLOBAL_VAR | LOCAL_VAR
  public static boolean variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable")) return false;
    if (!nextTokenIs(b, "<variable>", GLOBAL_VAR, LOCAL_VAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE, "<variable>");
    r = consumeToken(b, GLOBAL_VAR);
    if (!r) r = consumeToken(b, LOCAL_VAR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
