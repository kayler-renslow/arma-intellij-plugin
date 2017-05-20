// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SQFStatement extends PsiElement {

  @Nullable
  SQFAssignmentStatement getAssignmentStatement();

  @Nullable
  SQFCaseStatement getCaseStatement();

  @Nullable
  SQFExpression getExpression();

  @Nullable
  SQFQuestStatement getQuestStatement();

}
