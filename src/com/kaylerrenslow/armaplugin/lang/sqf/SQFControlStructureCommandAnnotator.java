package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.codestyle.SQFSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 12/28/2017
 */
public class SQFControlStructureCommandAnnotator implements Annotator {
	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		if (!(element instanceof SQFCommand)) {
			return;
		}
		SQFCommand command = (SQFCommand) element;
		switch (command.getCommandName().toLowerCase()) {
			case "if": //fall
			case "then": //fall
			case "else": //fall
			case "for": //fall
			case "foreach": //fall
			case "switch": //fall
			case "case": //fall
			case "default": //fall
			case "do": {
				Annotation annotation = holder.createInfoAnnotation(command, "");
				annotation.setTextAttributes(SQFSyntaxHighlighter.CONTROL_STRUCTURE_COMMAND);
				break;
			}
		}
	}
}
