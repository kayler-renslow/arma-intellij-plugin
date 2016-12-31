// This is a generated file. Not intended for manual editing.
package com.kaylerrenslow.a3plugin.lang.header.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HeaderPreprocessor extends PsiElement {

  @Nullable
  HeaderPreDefine getPreDefine();

  @Nullable
  HeaderPreExec getPreExec();

  @Nullable
  HeaderPreIfdef getPreIfdef();

  @Nullable
  HeaderPreIfndef getPreIfndef();

  @Nullable
  HeaderPreUndef getPreUndef();

}
