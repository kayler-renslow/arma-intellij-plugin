// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import java.util.ArrayList;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;

public interface SQFLocalScope extends SQFScope {

  @Nullable
  SQFCaseStatement getCaseStatement();

  @Nullable
  SQFExpression getExpression();

  @Nullable
  SQFQuestStatement getQuestStatement();

  @NotNull
  List<SQFStatement> getStatementList();

  List<SQFPrivateDeclVar> getPrivateVars();

  ArrayList<SQFStatement> getStatementsForScope();

  boolean checkIfSpawn();

  PsiElement getPrivatizerElement();

  SQFCodeBlock getCodeBlock();

}
