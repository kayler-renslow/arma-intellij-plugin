// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class SQFVisitor extends PsiElementVisitor {

  public void visitAddExpression(@NotNull SQFAddExpression o) {
    visitExpression(o);
  }

  public void visitArrayEntry(@NotNull SQFArrayEntry o) {
    visitPsiElement(o);
  }

  public void visitArrayVal(@NotNull SQFArrayVal o) {
    visitPsiElement(o);
  }

  public void visitAssignment(@NotNull SQFAssignment o) {
    visitPsiElement(o);
  }

  public void visitBoolAndExpression(@NotNull SQFBoolAndExpression o) {
    visitExpression(o);
  }

  public void visitBoolNotExpression(@NotNull SQFBoolNotExpression o) {
    visitExpression(o);
  }

  public void visitBoolOrExpression(@NotNull SQFBoolOrExpression o) {
    visitExpression(o);
  }

  public void visitCaseCommand(@NotNull SQFCaseCommand o) {
    visitCommand(o);
  }

  public void visitCaseStatement(@NotNull SQFCaseStatement o) {
    visitStatement(o);
  }

  public void visitCodeBlock(@NotNull SQFCodeBlock o) {
    visitExpression(o);
  }

  public void visitCommand(@NotNull SQFCommand o) {
    visitPsiElement(o);
  }

  public void visitCommandExpression(@NotNull SQFCommandExpression o) {
    visitExpression(o);
  }

  public void visitCompExpression(@NotNull SQFCompExpression o) {
    visitExpression(o);
  }

  public void visitConfigFetchExpression(@NotNull SQFConfigFetchExpression o) {
    visitExpression(o);
  }

  public void visitDivExpression(@NotNull SQFDivExpression o) {
    visitExpression(o);
  }

  public void visitExponentExpression(@NotNull SQFExponentExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull SQFExpression o) {
    visitPsiElement(o);
  }

  public void visitFileScope(@NotNull SQFFileScope o) {
    visitLocalScope(o);
  }

  public void visitLiteralExpression(@NotNull SQFLiteralExpression o) {
    visitExpression(o);
  }

  public void visitLocalScope(@NotNull SQFLocalScope o) {
    visitPsiElement(o);
  }

  public void visitMacroCall(@NotNull SQFMacroCall o) {
    visitPsiElement(o);
  }

  public void visitModExpression(@NotNull SQFModExpression o) {
    visitExpression(o);
  }

  public void visitMulExpression(@NotNull SQFMulExpression o) {
    visitExpression(o);
  }

  public void visitParenExpression(@NotNull SQFParenExpression o) {
    visitExpression(o);
  }

  public void visitPrivateCommand(@NotNull SQFPrivateCommand o) {
    visitCommand(o);
  }

  public void visitQuestStatement(@NotNull SQFQuestStatement o) {
    visitStatement(o);
  }

  public void visitReturnStatement(@NotNull SQFReturnStatement o) {
    visitStatement(o);
  }

  public void visitStatement(@NotNull SQFStatement o) {
    visitPsiElement(o);
  }

  public void visitString(@NotNull SQFString o) {
    visitPsiElement(o);
  }

  public void visitSubExpression(@NotNull SQFSubExpression o) {
    visitExpression(o);
  }

  public void visitUnaryExpression(@NotNull SQFUnaryExpression o) {
    visitExpression(o);
  }

  public void visitVariable(@NotNull SQFVariable o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
