// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.*;

public interface HeaderTypes {

  IElementType ARRAY = new HeaderElementType("ARRAY");
  IElementType ARRAY_ASSIGNMENT = new HeaderElementType("ARRAY_ASSIGNMENT");
  IElementType ARRAY_ENTRY_ = new HeaderElementType("ARRAY_ENTRY_");
  IElementType ASSIGNMENT = new HeaderElementType("ASSIGNMENT");
  IElementType EXPRESSION = new HeaderElementType("EXPRESSION");
  IElementType HEADER_CLASS = new HeaderElementType("HEADER_CLASS");
  IElementType PREPROCESSOR_COMMAND = new HeaderElementType("PREPROCESSOR_COMMAND");
  IElementType PREPROCESSOR_DEFINE = new HeaderElementType("PREPROCESSOR_DEFINE");
  IElementType PREPROCESSOR_ELSE = new HeaderElementType("PREPROCESSOR_ELSE");
  IElementType PREPROCESSOR_ENDIF = new HeaderElementType("PREPROCESSOR_ENDIF");
  IElementType PREPROCESSOR_IFDEF = new HeaderElementType("PREPROCESSOR_IFDEF");
  IElementType PREPROCESSOR_IFNDEF = new HeaderElementType("PREPROCESSOR_IFNDEF");
  IElementType PREPROCESSOR_INCLUDE = new HeaderElementType("PREPROCESSOR_INCLUDE");
  IElementType PREPROCESSOR_UNDEF = new HeaderElementType("PREPROCESSOR_UNDEF");

  IElementType ASTERISK = new HeaderTokenType("ASTERISK");
  IElementType BRACKET_PAIR = new HeaderTokenType("BRACKET_PAIR");
  IElementType CLASS = new HeaderTokenType("CLASS");
  IElementType COLON = new HeaderTokenType("COLON");
  IElementType COMMA = new HeaderTokenType("COMMA");
  IElementType EQ = new HeaderTokenType("EQ");
  IElementType FSLASH = new HeaderTokenType("FSLASH");
  IElementType HEX_LITERAL = new HeaderTokenType("HEX_LITERAL");
  IElementType IDENTIFIER = new HeaderTokenType("IDENTIFIER");
  IElementType INCLUDE_VALUE_ANGBR = new HeaderTokenType("INCLUDE_VALUE_ANGBR");
  IElementType LBRACE = new HeaderTokenType("LBRACE");
  IElementType LPAREN = new HeaderTokenType("LPAREN");
  IElementType MINUS = new HeaderTokenType("MINUS");
  IElementType NUMBER_LITERAL = new HeaderTokenType("NUMBER_LITERAL");
  IElementType PLUS = new HeaderTokenType("PLUS");
  IElementType PLUS_EQ = new HeaderTokenType("PLUS_EQ");
  IElementType PRE_DEFINE = new HeaderTokenType("PRE_DEFINE");
  IElementType PRE_DEFINE_BODY = new HeaderTokenType("PRE_DEFINE_BODY");
  IElementType PRE_ELSE = new HeaderTokenType("PRE_ELSE");
  IElementType PRE_ENDIF = new HeaderTokenType("PRE_ENDIF");
  IElementType PRE_IFDEF = new HeaderTokenType("PRE_IFDEF");
  IElementType PRE_IFNDEF = new HeaderTokenType("PRE_IFNDEF");
  IElementType PRE_INCLUDE = new HeaderTokenType("PRE_INCLUDE");
  IElementType PRE_UNDEF = new HeaderTokenType("PRE_UNDEF");
  IElementType RBRACE = new HeaderTokenType("RBRACE");
  IElementType RPAREN = new HeaderTokenType("RPAREN");
  IElementType SEMICOLON = new HeaderTokenType("SEMICOLON");
  IElementType STRING_LITERAL = new HeaderTokenType("STRING_LITERAL");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ARRAY) {
        return new HeaderArrayImpl(node);
      }
      else if (type == ARRAY_ASSIGNMENT) {
        return new HeaderArrayAssignmentImpl(node);
      }
      else if (type == ARRAY_ENTRY_) {
        return new HeaderArrayEntry_Impl(node);
      }
      else if (type == ASSIGNMENT) {
        return new HeaderAssignmentImpl(node);
      }
      else if (type == EXPRESSION) {
        return new HeaderExpressionImpl(node);
      }
      else if (type == HEADER_CLASS) {
        return new HeaderHeaderClassImpl(node);
      }
      else if (type == PREPROCESSOR_DEFINE) {
        return new HeaderPreprocessorDefineImpl(node);
      }
      else if (type == PREPROCESSOR_ELSE) {
        return new HeaderPreprocessorElseImpl(node);
      }
      else if (type == PREPROCESSOR_ENDIF) {
        return new HeaderPreprocessorEndifImpl(node);
      }
      else if (type == PREPROCESSOR_IFDEF) {
        return new HeaderPreprocessorIfdefImpl(node);
      }
      else if (type == PREPROCESSOR_IFNDEF) {
        return new HeaderPreprocessorIfndefImpl(node);
      }
      else if (type == PREPROCESSOR_INCLUDE) {
        return new HeaderPreprocessorIncludeImpl(node);
      }
      else if (type == PREPROCESSOR_UNDEF) {
        return new HeaderPreprocessorUndefImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
