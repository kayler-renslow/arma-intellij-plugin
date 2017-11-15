// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.impl.*;

public interface SQFTypes {

  IElementType ADD_EXPRESSION = new SQFElementType("ADD_EXPRESSION");
  IElementType ARRAY = new SQFElementType("ARRAY");
  IElementType ASSIGNMENT_STATEMENT = new SQFElementType("ASSIGNMENT_STATEMENT");
  IElementType BOOL_AND_EXPRESSION = new SQFElementType("BOOL_AND_EXPRESSION");
  IElementType BOOL_NOT_EXPRESSION = new SQFElementType("BOOL_NOT_EXPRESSION");
  IElementType BOOL_OR_EXPRESSION = new SQFElementType("BOOL_OR_EXPRESSION");
  IElementType CASE_COMMAND = new SQFElementType("CASE_COMMAND");
  IElementType CASE_STATEMENT = new SQFElementType("CASE_STATEMENT");
  IElementType CODE_BLOCK = new SQFElementType("CODE_BLOCK");
  IElementType CODE_BLOCK_EXPRESSION = new SQFElementType("CODE_BLOCK_EXPRESSION");
  IElementType COMMAND = new SQFElementType("COMMAND");
  IElementType COMMAND_AFTER = new SQFElementType("COMMAND_AFTER");
  IElementType COMMAND_BEFORE = new SQFElementType("COMMAND_BEFORE");
  IElementType COMMAND_EXPRESSION = new SQFElementType("COMMAND_EXPRESSION");
  IElementType COMP_EXPRESSION = new SQFElementType("COMP_EXPRESSION");
  IElementType CONFIG_FETCH_EXPRESSION = new SQFElementType("CONFIG_FETCH_EXPRESSION");
  IElementType DIV_EXPRESSION = new SQFElementType("DIV_EXPRESSION");
  IElementType EXPONENT_EXPRESSION = new SQFElementType("EXPONENT_EXPRESSION");
  IElementType EXPRESSION = new SQFElementType("EXPRESSION");
  IElementType EXPRESSION_STATEMENT = new SQFElementType("EXPRESSION_STATEMENT");
  IElementType FILE_SCOPE = new SQFElementType("FILE_SCOPE");
  IElementType LITERAL_EXPRESSION = new SQFElementType("LITERAL_EXPRESSION");
  IElementType LOCAL_SCOPE = new SQFElementType("LOCAL_SCOPE");
  IElementType MOD_EXPRESSION = new SQFElementType("MOD_EXPRESSION");
  IElementType MUL_EXPRESSION = new SQFElementType("MUL_EXPRESSION");
  IElementType PAREN_EXPRESSION = new SQFElementType("PAREN_EXPRESSION");
  IElementType PRIVATE_COMMAND = new SQFElementType("PRIVATE_COMMAND");
  IElementType QUEST_STATEMENT = new SQFElementType("QUEST_STATEMENT");
  IElementType SIGNED_EXPRESSION = new SQFElementType("SIGNED_EXPRESSION");
  IElementType STATEMENT = new SQFElementType("STATEMENT");
  IElementType STRING = new SQFElementType("STRING");
  IElementType SUB_EXPRESSION = new SQFElementType("SUB_EXPRESSION");
  IElementType VARIABLE = new SQFElementType("VARIABLE");

  IElementType AMPAMP = new SQFTokenType("AMPAMP");
  IElementType ASTERISK = new SQFTokenType("ASTERISK");
  IElementType BARBAR = new SQFTokenType("BARBAR");
  IElementType CARET = new SQFTokenType("CARET");
  IElementType COLON = new SQFTokenType("COLON");
  IElementType COMMA = new SQFTokenType("COMMA");
  IElementType COMMAND_TOKEN = new SQFTokenType("COMMAND_TOKEN");
  IElementType DEC_LITERAL = new SQFTokenType("DEC_LITERAL");
  IElementType EQ = new SQFTokenType("EQ");
  IElementType EQEQ = new SQFTokenType("EQEQ");
  IElementType EXCL = new SQFTokenType("EXCL");
  IElementType FSLASH = new SQFTokenType("FSLASH");
  IElementType GE = new SQFTokenType("GE");
  IElementType GLOBAL_VAR = new SQFTokenType("GLOBAL_VAR");
  IElementType GT = new SQFTokenType("GT");
  IElementType GTGT = new SQFTokenType("GTGT");
  IElementType HEX_LITERAL = new SQFTokenType("HEX_LITERAL");
  IElementType INTEGER_LITERAL = new SQFTokenType("INTEGER_LITERAL");
  IElementType LE = new SQFTokenType("LE");
  IElementType LOCAL_VAR = new SQFTokenType("LOCAL_VAR");
  IElementType LPAREN = new SQFTokenType("LPAREN");
  IElementType LT = new SQFTokenType("LT");
  IElementType L_CURLY_BRACE = new SQFTokenType("L_CURLY_BRACE");
  IElementType L_SQ_BRACKET = new SQFTokenType("L_SQ_BRACKET");
  IElementType MINUS = new SQFTokenType("MINUS");
  IElementType NE = new SQFTokenType("NE");
  IElementType PERC = new SQFTokenType("PERC");
  IElementType PLUS = new SQFTokenType("PLUS");
  IElementType QUEST = new SQFTokenType("QUEST");
  IElementType RPAREN = new SQFTokenType("RPAREN");
  IElementType R_CURLY_BRACE = new SQFTokenType("R_CURLY_BRACE");
  IElementType R_SQ_BRACKET = new SQFTokenType("R_SQ_BRACKET");
  IElementType SEMICOLON = new SQFTokenType("SEMICOLON");
  IElementType STRING_LITERAL = new SQFTokenType("STRING_LITERAL");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ADD_EXPRESSION) {
        return new SQFPsiAddExpressionImpl(node);
      }
      else if (type == ARRAY) {
        return new SQFPsiArrayImpl(node);
      }
      else if (type == ASSIGNMENT_STATEMENT) {
        return new SQFPsiAssignmentStatementImpl(node);
      }
      else if (type == BOOL_AND_EXPRESSION) {
        return new SQFPsiBoolAndExpressionImpl(node);
      }
      else if (type == BOOL_NOT_EXPRESSION) {
        return new SQFPsiBoolNotExpressionImpl(node);
      }
      else if (type == BOOL_OR_EXPRESSION) {
        return new SQFPsiBoolOrExpressionImpl(node);
      }
      else if (type == CASE_COMMAND) {
        return new SQFPsiCaseCommandImpl(node);
      }
      else if (type == CASE_STATEMENT) {
        return new SQFPsiCaseStatementImpl(node);
      }
      else if (type == CODE_BLOCK) {
        return new SQFPsiCodeBlockImpl(node);
      }
      else if (type == CODE_BLOCK_EXPRESSION) {
        return new SQFPsiCodeBlockExpressionImpl(node);
      }
      else if (type == COMMAND) {
        return new SQFPsiCommandImpl(node);
      }
      else if (type == COMMAND_AFTER) {
        return new SQFPsiCommandAfterImpl(node);
      }
      else if (type == COMMAND_BEFORE) {
        return new SQFPsiCommandBeforeImpl(node);
      }
      else if (type == COMMAND_EXPRESSION) {
        return new SQFPsiCommandExpressionImpl(node);
      }
      else if (type == COMP_EXPRESSION) {
        return new SQFPsiCompExpressionImpl(node);
      }
      else if (type == CONFIG_FETCH_EXPRESSION) {
        return new SQFPsiConfigFetchExpressionImpl(node);
      }
      else if (type == DIV_EXPRESSION) {
        return new SQFPsiDivExpressionImpl(node);
      }
      else if (type == EXPONENT_EXPRESSION) {
        return new SQFPsiExponentExpressionImpl(node);
      }
      else if (type == EXPRESSION_STATEMENT) {
        return new SQFPsiExpressionStatementImpl(node);
      }
      else if (type == FILE_SCOPE) {
        return new SQFPsiFileScopeImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new SQFPsiLiteralExpressionImpl(node);
      }
      else if (type == LOCAL_SCOPE) {
        return new SQFPsiLocalScopeImpl(node);
      }
      else if (type == MOD_EXPRESSION) {
        return new SQFPsiModExpressionImpl(node);
      }
      else if (type == MUL_EXPRESSION) {
        return new SQFPsiMulExpressionImpl(node);
      }
      else if (type == PAREN_EXPRESSION) {
        return new SQFPsiParenExpressionImpl(node);
      }
      else if (type == PRIVATE_COMMAND) {
        return new SQFPsiPrivateCommandImpl(node);
      }
      else if (type == QUEST_STATEMENT) {
        return new SQFPsiQuestStatementImpl(node);
      }
      else if (type == SIGNED_EXPRESSION) {
        return new SQFPsiSignedExpressionImpl(node);
      }
      else if (type == STRING) {
        return new SQFPsiStringImpl(node);
      }
      else if (type == SUB_EXPRESSION) {
        return new SQFPsiSubExpressionImpl(node);
      }
      else if (type == VARIABLE) {
        return new SQFPsiVariableImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
