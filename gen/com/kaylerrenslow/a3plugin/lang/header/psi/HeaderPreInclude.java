// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public interface HeaderPreInclude extends HeaderPreprocessor {

  @NotNull
  HeaderPreIncludeFile getPreIncludeFile();

  String getPathString();

  @Nullable
  PsiFile getHeaderFileFromInclude();

}
