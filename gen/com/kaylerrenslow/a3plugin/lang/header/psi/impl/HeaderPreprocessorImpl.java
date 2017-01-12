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

public class HeaderPreprocessorImpl extends ASTWrapperPsiElement implements HeaderPreprocessor {

  public HeaderPreprocessorImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitPreprocessor(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HeaderPreExec getPreExec() {
    return findChildByClass(HeaderPreExec.class);
  }

  @Override
  @Nullable
  public HeaderPreMacro getPreMacro() {
    return findChildByClass(HeaderPreMacro.class);
  }

}
