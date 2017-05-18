// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.header.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.kaylerrenslow.armaplugin.lang.header.psi.*;

public class HeaderArrayImpl extends ASTWrapperPsiElement implements HeaderArray {

  public HeaderArrayImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitArray(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<HeaderArrayEntry_> getArrayEntry_List() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HeaderArrayEntry_.class);
  }

}
