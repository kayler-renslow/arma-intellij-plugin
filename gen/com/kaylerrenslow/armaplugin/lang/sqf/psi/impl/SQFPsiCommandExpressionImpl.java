// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommandExpression;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFPsiCommandExpressionImpl extends SQFCommandExpression implements SQFPsiCommandExpression {

  public SQFPsiCommandExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFPsiVisitor visitor) {
    visitor.visitCommandExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFPsiVisitor) accept((SQFPsiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SQFPsiCommand getCommand() {
    return findNotNullChildByClass(SQFPsiCommand.class);
  }

  @Override
  @Nullable
  public SQFPsiCommandAfter getCommandAfter() {
    return findChildByClass(SQFPsiCommandAfter.class);
  }

  @Override
  @Nullable
  public SQFPsiCommandBefore getCommandBefore() {
    return findChildByClass(SQFPsiCommandBefore.class);
  }

}
