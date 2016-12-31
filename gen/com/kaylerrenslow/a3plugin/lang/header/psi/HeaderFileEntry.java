// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HeaderFileEntry extends PsiElement {

  @Nullable
  HeaderAssignment getAssignment();

  @Nullable
  HeaderClassDeclaration getClassDeclaration();

  @Nullable
  HeaderDefinedText getDefinedText();

  @Nullable
  HeaderMacroFunction getMacroFunction();

  @Nullable
  HeaderPreprocessorGroup getPreprocessorGroup();

}
