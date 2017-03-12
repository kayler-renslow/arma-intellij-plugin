// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import java.util.ArrayList;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;

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

  public List<SQFPrivateDeclVar> getPrivateVars() {
    return SQFPsiImplUtilForGrammar.getPrivateVars(this);
  }

  public ArrayList<SQFStatement> getStatementsForScope() {
    return SQFPsiImplUtilForGrammar.getStatementsForScope(this);
  }

  public boolean checkIfSpawn() {
    return SQFPsiImplUtilForGrammar.checkIfSpawn(this);
  }

  public PsiElement getPrivatizerElement() {
    return SQFPsiImplUtilForGrammar.getPrivatizerElement(this);
  }

  public SQFCodeBlock getCodeBlock() {
    return SQFPsiImplUtilForGrammar.getCodeBlock(this);
  }

}
