// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;

public class SQFLocalScopeImpl extends ASTWrapperPsiElement implements SQFLocalScope {

  public SQFLocalScopeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitLocalScope(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SQFStatement> getStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SQFStatement.class);
  }

}
