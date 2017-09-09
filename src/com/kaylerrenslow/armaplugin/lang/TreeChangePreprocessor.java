package com.kaylerrenslow.armaplugin.lang;

import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.kaylerrenslow.armaplugin.lang.header.psi.HeaderPsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 09/08/2017
 */
public class TreeChangePreprocessor implements PsiTreeChangePreprocessor {
	@Override
	public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
		if (event.getFile() instanceof HeaderPsiFile) {
			ArmaPluginUserData.getInstance().reparseRootConfig(event.getFile());
		}
	}
}
