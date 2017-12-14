// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class SQFPsiVisitor extends PsiElementVisitor {

  public void visitArray(@NotNull SQFPsiArray o) {
    visitPsiElement(o);
  }

  public void visitAssignmentStatement(@NotNull SQFPsiAssignmentStatement o) {
    visitStatement(o);
  }

  public void visitCaseCommand(@NotNull SQFPsiCaseCommand o) {
    visitCommand(o);
  }

  public void visitCaseStatement(@NotNull SQFPsiCaseStatement o) {
    visitStatement(o);
  }

  public void visitCodeBlock(@NotNull SQFPsiCodeBlock o) {
    visitPsiElement(o);
  }

  public void visitCodeBlockExpression(@NotNull SQFPsiCodeBlockExpression o) {
    visitExpression(o);
  }

  public void visitCommand(@NotNull SQFPsiCommand o) {
    visitPsiElement(o);
  }

  public void visitCommandAfter(@NotNull SQFPsiCommandAfter o) {
    visitPsiElement(o);
  }

  public void visitCommandBefore(@NotNull SQFPsiCommandBefore o) {
    visitPsiElement(o);
  }

  public void visitCommandExpression(@NotNull SQFPsiCommandExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull SQFPsiExpression o) {
    visitPsiElement(o);
  }

  public void visitExpressionOperator(@NotNull SQFPsiExpressionOperator o) {
    visitPsiElement(o);
  }

  public void visitExpressionStatement(@NotNull SQFPsiExpressionStatement o) {
    visitStatement(o);
  }

  public void visitFileScope(@NotNull SQFPsiFileScope o) {
    visitPsiElement(o);
  }

  public void visitLiteralExpression(@NotNull SQFPsiLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLocalScope(@NotNull SQFPsiLocalScope o) {
    visitPsiElement(o);
  }

  public void visitMacroCall(@NotNull SQFPsiMacroCall o) {
    visitPsiElement(o);
  }

  public void visitNumber(@NotNull SQFPsiNumber o) {
    visitPsiElement(o);
  }

  public void visitParenExpression(@NotNull SQFPsiParenExpression o) {
    visitExpression(o);
  }

  public void visitPrivateCommand(@NotNull SQFPsiPrivateCommand o) {
    visitCommand(o);
  }

  public void visitQuestStatement(@NotNull SQFPsiQuestStatement o) {
    visitStatement(o);
  }

  public void visitSignedExpression(@NotNull SQFPsiSignedExpression o) {
    visitExpression(o);
  }

  public void visitStatement(@NotNull SQFPsiStatement o) {
    visitPsiElement(o);
  }

  public void visitString(@NotNull SQFPsiString o) {
    visitPsiElement(o);
  }

  public void visitVariable(@NotNull SQFPsiVariable o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
