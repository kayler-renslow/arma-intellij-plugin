// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableBase;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatization;

public interface SQFVariable extends SQFVariableNamedElement {

  boolean followsSQFFunctionNameRules();

  @NotNull
  SQFScope getDeclarationScope();

  boolean isAssigningVariable();

  SQFAssignment getMyAssignment();

  @Nullable
  SQFPrivatization getPrivatization();

  boolean varNameMatches(String otherName);

  boolean varNameMatches(SQFVariableBase variable2);

}
