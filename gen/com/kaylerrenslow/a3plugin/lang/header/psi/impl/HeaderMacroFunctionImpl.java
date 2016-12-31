// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.kaylerrenslow.a3plugin.lang.header.psi.*;

public class HeaderMacroFunctionImpl extends ASTWrapperPsiElement implements HeaderMacroFunction {

  public HeaderMacroFunctionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitMacroFunction(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<HeaderArray> getArrayList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HeaderArray.class);
  }

  @Override
  @NotNull
  public List<HeaderMacroFunction> getMacroFunctionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HeaderMacroFunction.class);
  }

  @Override
  @Nullable
  public HeaderPreEval getPreEval() {
    return findChildByClass(HeaderPreEval.class);
  }

}
