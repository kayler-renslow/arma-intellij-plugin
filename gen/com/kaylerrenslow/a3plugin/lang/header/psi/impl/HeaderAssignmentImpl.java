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

public abstract class HeaderAssignmentImpl extends ASTWrapperPsiElement implements HeaderAssignment {

  public HeaderAssignmentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HeaderVisitor visitor) {
    visitor.visitAssignment(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HeaderVisitor) accept((HeaderVisitor)visitor);
    else super.accept(visitor);
  }

  public String getAssigningVariable() {
    return HeaderPsiUtilForGrammar.getAssigningVariable(this);
  }

  public String getAssigningValue() {
    return HeaderPsiUtilForGrammar.getAssigningValue(this);
  }

  public ASTNode getAssigningIdentifierNode() {
    return HeaderPsiUtilForGrammar.getAssigningIdentifierNode(this);
  }

}
