// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SQFAssignment extends PsiElement {

  @Nullable
  SQFExpression getExpression();

  @Nullable
  SQFPrivateCommand getPrivateCommand();

  @NotNull
  SQFVariable getVariable();

  @NotNull
  SQFVariable getAssigningVariable();

  boolean isDeclaredPrivate();

}
