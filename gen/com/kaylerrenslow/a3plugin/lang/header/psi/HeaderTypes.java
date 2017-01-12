// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.*;

public interface HeaderTypes {

  IElementType ARRAY = new HeaderElementType("ARRAY");
  IElementType ARRAY_ASSIGNMENT = new HeaderElementType("ARRAY_ASSIGNMENT");
  IElementType ARRAY_BODY = new HeaderElementType("ARRAY_BODY");
  IElementType ARRAY_ENTRY = new HeaderElementType("ARRAY_ENTRY");
  IElementType ASSIGNMENT = new HeaderElementType("ASSIGNMENT");
  IElementType ASSIGNMENT_IDENTIFIER = new HeaderElementType("ASSIGNMENT_IDENTIFIER");
  IElementType BASIC_ASSIGNMENT = new HeaderElementType("BASIC_ASSIGNMENT");
  IElementType CLASS_CONTENT = new HeaderElementType("CLASS_CONTENT");
  IElementType CLASS_DECLARATION = new HeaderElementType("CLASS_DECLARATION");
  IElementType CLASS_STUB = new HeaderElementType("CLASS_STUB");
  IElementType DEFINED_TEXT = new HeaderElementType("DEFINED_TEXT");
  IElementType EXPRESSION = new HeaderElementType("EXPRESSION");
  IElementType FILE_ENTRIES = new HeaderElementType("FILE_ENTRIES");
  IElementType FILE_ENTRY = new HeaderElementType("FILE_ENTRY");
  IElementType MACRO_FUNCTION = new HeaderElementType("MACRO_FUNCTION");
  IElementType PREPROCESSOR = new HeaderElementType("PREPROCESSOR");
  IElementType PREPROCESSOR_GROUP = new HeaderElementType("PREPROCESSOR_GROUP");
  IElementType PRE_EVAL = new HeaderElementType("PRE_EVAL");
  IElementType PRE_EXEC = new HeaderElementType("PRE_EXEC");
  IElementType PRE_INCLUDE = new HeaderElementType("PRE_INCLUDE");
  IElementType PRE_INCLUDE_FILE = new HeaderElementType("PRE_INCLUDE_FILE");
  IElementType PRE_MACRO = new HeaderElementType("PRE_MACRO");
  IElementType STRINGTABLE_KEY = new HeaderElementType("STRINGTABLE_KEY");

  IElementType ASTERISK = new HeaderTokenType("ASTERISK");
  IElementType BLOCK_COMMENT = new HeaderTokenType("BLOCK_COMMENT");
  IElementType BRACKET_PAIR = new HeaderTokenType("BRACKET_PAIR");
  IElementType CLASS = new HeaderTokenType("CLASS");
  IElementType COLON = new HeaderTokenType("COLON");
  IElementType COMMA = new HeaderTokenType("COMMA");
  IElementType EQ = new HeaderTokenType("EQ");
  IElementType FSLASH = new HeaderTokenType("FSLASH");
  IElementType HEX_LITERAL = new HeaderTokenType("HEX_LITERAL");
  IElementType IDENTIFIER = new HeaderTokenType("IDENTIFIER");
  IElementType INCLUDE_VALUE_ANGBR = new HeaderTokenType("INCLUDE_VALUE_ANGBR");
  IElementType INLINE_COMMENT = new HeaderTokenType("INLINE_COMMENT");
  IElementType LBRACE = new HeaderTokenType("LBRACE");
  IElementType LPAREN = new HeaderTokenType("LPAREN");
  IElementType MINUS = new HeaderTokenType("MINUS");
  IElementType NUMBER_LITERAL = new HeaderTokenType("NUMBER_LITERAL");
  IElementType PLUS = new HeaderTokenType("PLUS");
  IElementType PREPROCESS_EVAL = new HeaderTokenType("PREPROCESS_EVAL");
  IElementType PREPROCESS_EXEC = new HeaderTokenType("PREPROCESS_EXEC");
  IElementType PREPROCESS_INCLUDE = new HeaderTokenType("PREPROCESS_INCLUDE");
  IElementType PREPROCESS_MACRO = new HeaderTokenType("PREPROCESS_MACRO");
  IElementType RBRACE = new HeaderTokenType("RBRACE");
  IElementType RPAREN = new HeaderTokenType("RPAREN");
  IElementType SEMICOLON = new HeaderTokenType("SEMICOLON");
  IElementType STRINGTABLE_ENTRY = new HeaderTokenType("STRINGTABLE_ENTRY");
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
      else if (type == ARRAY_BODY) {
        return new HeaderArrayBodyImpl(node);
      }
      else if (type == ARRAY_ENTRY) {
        return new HeaderArrayEntryImpl(node);
      }
      else if (type == ASSIGNMENT_IDENTIFIER) {
        return new HeaderAssignmentIdentifierImpl(node);
      }
      else if (type == BASIC_ASSIGNMENT) {
        return new HeaderBasicAssignmentImpl(node);
      }
      else if (type == CLASS_CONTENT) {
        return new HeaderClassContentImpl(node);
      }
      else if (type == CLASS_DECLARATION) {
        return new HeaderClassDeclarationImpl(node);
      }
      else if (type == CLASS_STUB) {
        return new HeaderClassStubImpl(node);
      }
      else if (type == DEFINED_TEXT) {
        return new HeaderDefinedTextImpl(node);
      }
      else if (type == EXPRESSION) {
        return new HeaderExpressionImpl(node);
      }
      else if (type == FILE_ENTRIES) {
        return new HeaderFileEntriesImpl(node);
      }
      else if (type == FILE_ENTRY) {
        return new HeaderFileEntryImpl(node);
      }
      else if (type == MACRO_FUNCTION) {
        return new HeaderMacroFunctionImpl(node);
      }
      else if (type == PREPROCESSOR) {
        return new HeaderPreprocessorImpl(node);
      }
      else if (type == PREPROCESSOR_GROUP) {
        return new HeaderPreprocessorGroupImpl(node);
      }
      else if (type == PRE_EVAL) {
        return new HeaderPreEvalImpl(node);
      }
      else if (type == PRE_EXEC) {
        return new HeaderPreExecImpl(node);
      }
      else if (type == PRE_INCLUDE) {
        return new HeaderPreIncludeImpl(node);
      }
      else if (type == PRE_INCLUDE_FILE) {
        return new HeaderPreIncludeFileImpl(node);
      }
      else if (type == PRE_MACRO) {
        return new HeaderPreMacroImpl(node);
      }
      else if (type == STRINGTABLE_KEY) {
        return new HeaderStringtableKeyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
