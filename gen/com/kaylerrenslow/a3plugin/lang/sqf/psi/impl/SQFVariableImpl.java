// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElementMixin;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableBase;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatization;

public class SQFVariableImpl extends SQFVariableNamedElementMixin implements SQFVariable {

  public SQFVariableImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SQFVisitor visitor) {
    visitor.visitVariable(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SQFVisitor) accept((SQFVisitor)visitor);
    else super.accept(visitor);
  }

  public boolean followsSQFFunctionNameRules() {
    return SQFPsiImplUtilForGrammar.followsSQFFunctionNameRules(this);
  }

  @NotNull
  public SQFScope getDeclarationScope() {
    return SQFPsiImplUtilForGrammar.getDeclarationScope(this);
  }

  public boolean isAssigningVariable() {
    return SQFPsiImplUtilForGrammar.isAssigningVariable(this);
  }

  public SQFAssignment getMyAssignment() {
    return SQFPsiImplUtilForGrammar.getMyAssignment(this);
  }

  @Nullable
  public SQFPrivatization getPrivatization() {
    return SQFPsiImplUtilForGrammar.getPrivatization(this);
  }

  public boolean varNameMatches(String otherName) {
    return SQFPsiImplUtilForGrammar.varNameMatches(this, otherName);
  }

  public boolean varNameMatches(SQFVariableBase variable2) {
    return SQFPsiImplUtilForGrammar.varNameMatches(this, variable2);
  }

}
