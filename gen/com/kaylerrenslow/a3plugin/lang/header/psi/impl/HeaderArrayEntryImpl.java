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

public class HeaderArrayEntryImpl extends ASTWrapperPsiElement implements HeaderArrayEntry {

  public HeaderArrayEntryImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitArrayEntry(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HeaderArray getArray() {
    return findChildByClass(HeaderArray.class);
  }

  @Override
  @Nullable
  public HeaderExpression getExpression() {
    return findChildByClass(HeaderExpression.class);
  }

  @Override
  @Nullable
  public HeaderMacroFunction getMacroFunction() {
    return findChildByClass(HeaderMacroFunction.class);
  }

  @Override
  @Nullable
  public HeaderStringtableKey getStringtableKey() {
    return findChildByClass(HeaderStringtableKey.class);
  }

}
