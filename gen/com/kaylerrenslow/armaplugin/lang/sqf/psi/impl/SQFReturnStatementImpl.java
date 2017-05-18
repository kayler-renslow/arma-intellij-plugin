// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFReturnStatementImpl extends SQFStatementImpl implements SQFReturnStatement {

  public SQFReturnStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitReturnStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SQFAssignmentStatement getAssignmentStatement() {
    return findChildByClass(SQFAssignmentStatement.class);
  }

  @Override
  @Nullable
  public SQFCaseStatement getCaseStatement() {
    return findChildByClass(SQFCaseStatement.class);
  }

  @Override
  @Nullable
  public SQFQuestStatement getQuestStatement() {
    return findChildByClass(SQFQuestStatement.class);
  }

  @Override
  @Nullable
  public SQFVariable getVariable() {
    return findChildByClass(SQFVariable.class);
  }

}
