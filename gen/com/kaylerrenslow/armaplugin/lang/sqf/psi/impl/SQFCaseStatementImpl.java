// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFCaseStatementImpl extends SQFStatementImpl implements SQFCaseStatement {

  public SQFCaseStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitCaseStatement(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SQFCaseCommand getCaseCommand() {
    return findNotNullChildByClass(SQFCaseCommand.class);
  }

  @Override
  @Nullable
  public SQFCodeBlock getCodeBlock() {
    return findChildByClass(SQFCodeBlock.class);
  }

}
