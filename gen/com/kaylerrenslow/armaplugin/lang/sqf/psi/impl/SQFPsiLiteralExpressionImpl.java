// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFLiteralExpression;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFPsiLiteralExpressionImpl extends SQFLiteralExpression implements SQFPsiLiteralExpression {

  public SQFPsiLiteralExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFPsiVisitor visitor) {
    visitor.visitLiteralExpression(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFPsiVisitor) accept((SQFPsiVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SQFPsiArray getArray() {
    return findChildByClass(SQFPsiArray.class);
  }

  @Override
  @Nullable
  public SQFPsiMacroCall getMacroCall() {
    return findChildByClass(SQFPsiMacroCall.class);
  }

  @Override
  @Nullable
  public SQFPsiNumber getNumber() {
    return findChildByClass(SQFPsiNumber.class);
  }

  @Override
  @Nullable
  public SQFPsiString getString() {
    return findChildByClass(SQFPsiString.class);
  }

  @Override
  @Nullable
  public SQFPsiVariable getVariable() {
    return findChildByClass(SQFPsiVariable.class);
  }

}
