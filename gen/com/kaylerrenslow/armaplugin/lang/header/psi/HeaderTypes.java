// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;

public interface HeaderTypes {


  IElementType ASTERISK = new HeaderTokenType("ASTERISK");
  IElementType BRACKET_PAIR = new HeaderTokenType("BRACKET_PAIR");
  IElementType CLASS = new HeaderTokenType("CLASS");
  IElementType COLON = new HeaderTokenType("COLON");
  IElementType COMMA = new HeaderTokenType("COMMA");
  IElementType EQ = new HeaderTokenType("EQ");
  IElementType FSLASH = new HeaderTokenType("FSLASH");
  IElementType HEX_LITERAL = new HeaderTokenType("HEX_LITERAL");
  IElementType IDENTIFIER = new HeaderTokenType("IDENTIFIER");
  IElementType LBRACE = new HeaderTokenType("LBRACE");
  IElementType LPAREN = new HeaderTokenType("LPAREN");
  IElementType MACRO = new HeaderTokenType("MACRO");
  IElementType MINUS = new HeaderTokenType("MINUS");
  IElementType NUMBER_LITERAL = new HeaderTokenType("NUMBER_LITERAL");
  IElementType PLUS = new HeaderTokenType("PLUS");
  IElementType PLUS_EQ = new HeaderTokenType("PLUS_EQ");
  IElementType RBRACE = new HeaderTokenType("RBRACE");
  IElementType RPAREN = new HeaderTokenType("RPAREN");
  IElementType SEMICOLON = new HeaderTokenType("SEMICOLON");
  IElementType STRING_LITERAL = new HeaderTokenType("STRING_LITERAL");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
