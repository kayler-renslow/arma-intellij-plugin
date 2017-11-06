// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SQFPsiAssignmentStatement extends SQFPsiStatement {

  @Nullable
  SQFPsiExpression getExpression();

  @Nullable
  SQFPsiPrivateCommand getPrivateCommand();

  @NotNull
  SQFPsiVariable getVariable();

}
