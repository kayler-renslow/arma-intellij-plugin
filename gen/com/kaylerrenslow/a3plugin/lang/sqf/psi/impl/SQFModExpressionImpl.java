// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;

public class SQFModExpressionImpl extends SQFExpressionImpl implements SQFModExpression {

  public SQFModExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitModExpression(this);
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
