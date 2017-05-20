// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SQFCaseStatement extends PsiElement {

  @NotNull
  SQFCaseCommand getCaseCommand();

  @Nullable
  SQFCodeBlock getCodeBlock();

  @NotNull
  SQFExpression getExpression();

}
