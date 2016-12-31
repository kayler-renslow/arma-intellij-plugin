// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFStringMixin;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.intellij.openapi.util.TextRange;

public class SQFStringImpl extends SQFStringMixin implements SQFString {

  public SQFStringImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitString(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  public TextRange getNonQuoteRangeRelativeToFile() {
    return SQFPsiImplUtilForGrammar.getNonQuoteRangeRelativeToFile(this);
  }

  public TextRange getNonQuoteRangeRelativeToElement() {
    return SQFPsiImplUtilForGrammar.getNonQuoteRangeRelativeToElement(this);
  }

}
