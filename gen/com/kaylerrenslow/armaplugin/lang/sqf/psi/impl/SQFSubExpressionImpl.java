// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFSubExpressionImpl extends SQFExpressionImpl implements SQFSubExpression {

  public SQFSubExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitSubExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SQFExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SQFExpression.class);
  }

}
