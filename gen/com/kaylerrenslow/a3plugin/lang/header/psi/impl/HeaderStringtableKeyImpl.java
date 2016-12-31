// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes.*;
import com.kaylerrenslow.a3plugin.lang.header.psi.mixin.HeaderStringtableKeyMixin;
import com.kaylerrenslow.a3plugin.lang.header.psi.*;

public class HeaderStringtableKeyImpl extends HeaderStringtableKeyMixin implements HeaderStringtableKey {

  public HeaderStringtableKeyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitStringtableKey(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  public String getKey() {
    return HeaderPsiUtilForGrammar.getKey(this);
  }

}
