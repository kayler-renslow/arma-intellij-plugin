// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.kaylerrenslow.a3plugin.util.Attribute;

public interface HeaderClassDeclaration extends PsiElement {

  @Nullable
  HeaderClassContent getClassContent();

  @NotNull
  HeaderClassStub getClassStub();

  String getClassName();

  String getExtendClassName();

  Attribute[] getAttributes(boolean traverseIncludes);

  boolean hasAttributes(Attribute[] attributes, boolean traverseIncludes);

  void setAttribute(String attribute, String newValue);

  ASTNode getClassNameNode();

  void removeBracesIfEmpty();

  boolean bracesAreEmpty();

  void removeFromTree();

  void createClassContent();

  HeaderFileEntry addFileEntry(String textWithoutSemicolon);

  HeaderClassDeclaration addClassDeclaration(String newClassDeclName, Attribute[] attributes);

}
