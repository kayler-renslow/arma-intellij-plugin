// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFCodeBlockImpl extends ASTWrapperPsiElement implements SQFCodeBlock {

  public SQFCodeBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitCodeBlock(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SQFLocalScope getLocalScope() {
    return findChildByClass(SQFLocalScope.class);
  }

}
