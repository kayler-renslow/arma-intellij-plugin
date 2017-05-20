// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFCommandExpressionImpl extends SQFExpressionImpl implements SQFCommandExpression {

  public SQFCommandExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitCommandExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SQFCommand getCommand() {
    return findNotNullChildByClass(SQFCommand.class);
  }

  @Override
  @Nullable
  public SQFCommandAfter getCommandAfter() {
    return findChildByClass(SQFCommandAfter.class);
  }

  @Override
  @Nullable
  public SQFCommandBefore getCommandBefore() {
    return findChildByClass(SQFCommandBefore.class);
  }

}
