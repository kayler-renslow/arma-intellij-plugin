// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.header.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class HeaderVisitor extends PsiElementVisitor {

  public void visitArray(@NotNull HeaderArray o) {
    visitPsiElement(o);
  }

  public void visitArrayAssignment(@NotNull HeaderArrayAssignment o) {
    visitPsiElement(o);
  }

  public void visitArrayEntry_(@NotNull HeaderArrayEntry_ o) {
    visitPsiElement(o);
  }

  public void visitAssignment(@NotNull HeaderAssignment o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull HeaderExpression o) {
    visitPsiElement(o);
  }

  public void visitHeaderClass(@NotNull HeaderHeaderClass o) {
    visitPsiElement(o);
  }

  public void visitPreprocessorCommand(@NotNull HeaderPreprocessorCommand o) {
    visitPsiElement(o);
  }

  public void visitPreprocessorDefine(@NotNull HeaderPreprocessorDefine o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorElse(@NotNull HeaderPreprocessorElse o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorEndif(@NotNull HeaderPreprocessorEndif o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorIfdef(@NotNull HeaderPreprocessorIfdef o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorIfndef(@NotNull HeaderPreprocessorIfndef o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorInclude(@NotNull HeaderPreprocessorInclude o) {
    visitPreprocessorCommand(o);
  }

  public void visitPreprocessorUndef(@NotNull HeaderPreprocessorUndef o) {
    visitPreprocessorCommand(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
