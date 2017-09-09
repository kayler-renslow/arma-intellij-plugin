package com.kaylerrenslow.armaplugin.lang.header.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.kaylerrenslow.armaplugin.lang.header.HeaderFileType;
import com.kaylerrenslow.armaplugin.lang.header.HeaderLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * PsiFile extension point for Header language.
 *
 * @author Kayler
 * @since 10/31/2015
 */
public class HeaderPsiFile extends PsiFileBase {
	public HeaderPsiFile(FileViewProvider viewProvider) {
		super(viewProvider, HeaderLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType() {
		return HeaderFileType.INSTANCE;
	}

	@Override
	public Icon getIcon(int flags) {
		return super.getIcon(flags);
	}
}
