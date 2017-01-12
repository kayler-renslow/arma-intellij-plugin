// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public class HeaderVisitor extends PsiElementVisitor {

  public void visitArray(@NotNull HeaderArray o) {
    visitPsiElement(o);
  }

  public void visitArrayAssignment(@NotNull HeaderArrayAssignment o) {
    visitAssignment(o);
  }

  public void visitArrayBody(@NotNull HeaderArrayBody o) {
    visitPsiElement(o);
  }

  public void visitArrayEntry(@NotNull HeaderArrayEntry o) {
    visitPsiElement(o);
  }

  public void visitAssignment(@NotNull HeaderAssignment o) {
    visitPsiElement(o);
  }

  public void visitAssignmentIdentifier(@NotNull HeaderAssignmentIdentifier o) {
    visitPsiElement(o);
  }

  public void visitBasicAssignment(@NotNull HeaderBasicAssignment o) {
    visitAssignment(o);
  }

  public void visitClassContent(@NotNull HeaderClassContent o) {
    visitPsiElement(o);
  }

  public void visitClassDeclaration(@NotNull HeaderClassDeclaration o) {
    visitPsiElement(o);
  }

  public void visitClassStub(@NotNull HeaderClassStub o) {
    visitPsiNamedElement(o);
  }

  public void visitDefinedText(@NotNull HeaderDefinedText o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull HeaderExpression o) {
    visitPsiElement(o);
  }

  public void visitFileEntries(@NotNull HeaderFileEntries o) {
    visitPsiElement(o);
  }

  public void visitFileEntry(@NotNull HeaderFileEntry o) {
    visitPsiElement(o);
  }

  public void visitMacroFunction(@NotNull HeaderMacroFunction o) {
    visitPsiElement(o);
  }

  public void visitPreEval(@NotNull HeaderPreEval o) {
    visitPsiElement(o);
  }

  public void visitPreExec(@NotNull HeaderPreExec o) {
    visitPsiElement(o);
  }

  public void visitPreInclude(@NotNull HeaderPreInclude o) {
    visitPreprocessor(o);
  }

  public void visitPreIncludeFile(@NotNull HeaderPreIncludeFile o) {
    visitPsiElement(o);
  }

  public void visitPreMacro(@NotNull HeaderPreMacro o) {
    visitPsiElement(o);
  }

  public void visitPreprocessor(@NotNull HeaderPreprocessor o) {
    visitPsiElement(o);
  }

  public void visitPreprocessorGroup(@NotNull HeaderPreprocessorGroup o) {
    visitPsiElement(o);
  }

  public void visitStringtableKey(@NotNull HeaderStringtableKey o) {
    visitPsiNamedElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
