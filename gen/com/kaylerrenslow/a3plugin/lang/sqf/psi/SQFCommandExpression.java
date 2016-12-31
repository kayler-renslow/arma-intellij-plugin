// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SQFCommandExpression extends SQFExpression {

  @NotNull
  SQFCommand getCommand();

  @NotNull
  List<SQFExpression> getExpressionList();

  String getCommandName();

  @Nullable
  PsiElement getPrefixArgument();

  @Nullable
  PsiElement getPostfixArgument();

}
