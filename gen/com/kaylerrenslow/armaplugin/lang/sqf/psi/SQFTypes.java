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
  IElementType FILE_SCOPE = new SQFElementType("FILE_SCOPE");
  IElementType LITERAL_EXPRESSION = new SQFElementType("LITERAL_EXPRESSION");
  IElementType LOCAL_SCOPE = new SQFElementType("LOCAL_SCOPE");
  IElementType MOD_EXPRESSION = new SQFElementType("MOD_EXPRESSION");
  IElementType MUL_EXPRESSION = new SQFElementType("MUL_EXPRESSION");
  IElementType PAREN_EXPRESSION = new SQFElementType("PAREN_EXPRESSION");
  IElementType PRIVATE_COMMAND = new SQFElementType("PRIVATE_COMMAND");
  IElementType QUEST_STATEMENT = new SQFElementType("QUEST_STATEMENT");
  IElementType RETURN_STATEMENT = new SQFElementType("RETURN_STATEMENT");
  IElementType STATEMENT = new SQFElementType("STATEMENT");
  IElementType STRING = new SQFElementType("STRING");
  IElementType SUB_EXPRESSION = new SQFElementType("SUB_EXPRESSION");
  IElementType UNARY_EXPRESSION = new SQFElementType("UNARY_EXPRESSION");
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
  IElementType LANG_VAR = new SQFTokenType("LANG_VAR");
  IElementType LBRACE = new SQFTokenType("LBRACE");
  IElementType LBRACKET = new SQFTokenType("LBRACKET");
  IElementType LE = new SQFTokenType("LE");
  IElementType LOCAL_VAR = new SQFTokenType("LOCAL_VAR");
  IElementType LPAREN = new SQFTokenType("LPAREN");
  IElementType LT = new SQFTokenType("LT");
  IElementType MINUS = new SQFTokenType("MINUS");
  IElementType NE = new SQFTokenType("NE");
  IElementType PERC = new SQFTokenType("PERC");
  IElementType PLUS = new SQFTokenType("PLUS");
  IElementType QUEST = new SQFTokenType("QUEST");
  IElementType RBRACE = new SQFTokenType("RBRACE");
  IElementType RBRACKET = new SQFTokenType("RBRACKET");
  IElementType RPAREN = new SQFTokenType("RPAREN");
  IElementType SEMICOLON = new SQFTokenType("SEMICOLON");
  IElementType STRING_LITERAL = new SQFTokenType("STRING_LITERAL");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ADD_EXPRESSION) {
        return new SQFAddExpressionImpl(node);
      }
      else if (type == ARRAY) {
        return new SQFArrayImpl(node);
      }
      else if (type == ASSIGNMENT_STATEMENT) {
        return new SQFAssignmentStatementImpl(node);
      }
      else if (type == BOOL_AND_EXPRESSION) {
        return new SQFBoolAndExpressionImpl(node);
      }
      else if (type == BOOL_NOT_EXPRESSION) {
        return new SQFBoolNotExpressionImpl(node);
      }
      else if (type == BOOL_OR_EXPRESSION) {
        return new SQFBoolOrExpressionImpl(node);
      }
      else if (type == CASE_COMMAND) {
        return new SQFCaseCommandImpl(node);
      }
      else if (type == CASE_STATEMENT) {
        return new SQFCaseStatementImpl(node);
      }
      else if (type == CODE_BLOCK) {
        return new SQFCodeBlockImpl(node);
      }
      else if (type == CODE_BLOCK_EXPRESSION) {
        return new SQFCodeBlockExpressionImpl(node);
      }
      else if (type == COMMAND) {
        return new SQFCommandImpl(node);
      }
      else if (type == COMMAND_AFTER) {
        return new SQFCommandAfterImpl(node);
      }
      else if (type == COMMAND_BEFORE) {
        return new SQFCommandBeforeImpl(node);
      }
      else if (type == COMMAND_EXPRESSION) {
        return new SQFCommandExpressionImpl(node);
      }
      else if (type == COMP_EXPRESSION) {
        return new SQFCompExpressionImpl(node);
      }
      else if (type == CONFIG_FETCH_EXPRESSION) {
        return new SQFConfigFetchExpressionImpl(node);
      }
      else if (type == DIV_EXPRESSION) {
        return new SQFDivExpressionImpl(node);
      }
      else if (type == EXPONENT_EXPRESSION) {
        return new SQFExponentExpressionImpl(node);
      }
      else if (type == FILE_SCOPE) {
        return new SQFFileScopeImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new SQFLiteralExpressionImpl(node);
      }
      else if (type == LOCAL_SCOPE) {
        return new SQFLocalScopeImpl(node);
      }
      else if (type == MOD_EXPRESSION) {
        return new SQFModExpressionImpl(node);
      }
      else if (type == MUL_EXPRESSION) {
        return new SQFMulExpressionImpl(node);
      }
      else if (type == PAREN_EXPRESSION) {
        return new SQFParenExpressionImpl(node);
      }
      else if (type == PRIVATE_COMMAND) {
        return new SQFPrivateCommandImpl(node);
      }
      else if (type == QUEST_STATEMENT) {
        return new SQFQuestStatementImpl(node);
      }
      else if (type == RETURN_STATEMENT) {
        return new SQFReturnStatementImpl(node);
      }
      else if (type == STATEMENT) {
        return new SQFStatementImpl(node);
      }
      else if (type == STRING) {
        return new SQFStringImpl(node);
      }
      else if (type == SUB_EXPRESSION) {
        return new SQFSubExpressionImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new SQFUnaryExpressionImpl(node);
      }
      else if (type == VARIABLE) {
        return new SQFVariableImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
