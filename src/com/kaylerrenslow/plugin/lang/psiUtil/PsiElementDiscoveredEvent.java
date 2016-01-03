package com.kaylerrenslow.plugin.lang.psiUtil;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by Kayler on 01/02/2016.
 */
public abstract class PsiElementDiscoveredEvent{
	public abstract void elementDiscovered(PsiFile file, PsiElement element);
}
