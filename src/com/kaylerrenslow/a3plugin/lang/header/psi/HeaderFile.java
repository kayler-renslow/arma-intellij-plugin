package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.kaylerrenslow.a3plugin.lang.header.HeaderLanguage;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderFile extends PsiFileBase{
	public HeaderFile(FileViewProvider viewProvider) {
		super(viewProvider, HeaderLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType(){
		return HeaderFileType.INSTANCE;
	}

	/*
	@Override
	public String toString(){
		return "SQF File";
	}
	*/

	@Override
	public Icon getIcon(int flags) {
		return super.getIcon(flags);
	}
}
