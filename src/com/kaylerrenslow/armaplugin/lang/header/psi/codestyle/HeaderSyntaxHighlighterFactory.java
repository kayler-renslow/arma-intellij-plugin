package com.kaylerrenslow.armaplugin.lang.header.psi.codestyle;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * SyntaxHighlighterFactory implementation for Header language
 *
 * @author Kayler
 * @since 09/11/2017
 */
public class HeaderSyntaxHighlighterFactory extends SyntaxHighlighterFactory {

	@NotNull
	@Override
	public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
		return new HeaderSyntaxHighlighter();
	}
}
