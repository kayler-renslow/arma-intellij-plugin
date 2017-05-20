package com.kaylerrenslow.armaplugin.lang.sqf.psi.codestyle;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * SyntaxHighlighterFactory extension point for SQF language.
 * This class is referenced and loaded via the plugin.xml
 *
 * @author Kayler
 * @since 11/01/2015
 */
public class SQFSyntaxHighlighterFactory extends SyntaxHighlighterFactory {

	@NotNull
	@Override
	public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
		return new SQFSyntaxHighlighter();
	}

}