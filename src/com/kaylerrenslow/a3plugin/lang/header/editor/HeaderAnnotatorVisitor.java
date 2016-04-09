package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFileEntries;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderVisitor;
import com.kaylerrenslow.a3plugin.lang.shared.SharedSyntaxHighlighterColors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 * Annotator for Header language
 * Created on 04/08/2016.
 */
public class HeaderAnnotatorVisitor extends HeaderVisitor {

	private AnnotationHolder holder;

	public void setAnnotationHolder(AnnotationHolder holder) {
		this.holder = holder;
	}

	@Override
	public void visitFileEntries(@NotNull HeaderFileEntries entries) {
		super.visitFileEntries(entries);
		ArrayList<HeaderClassDeclaration> declarations = HeaderPsiUtil.getClassDeclarationsWithEntriesEqual(entries, null, null, false, 1,1);

		//check for duplicate class declarations
		HeaderClassDeclaration classDeclarationMatch;
		ArrayList<String> classDeclarationNames = new ArrayList<>();
		for (HeaderClassDeclaration classDeclaration : declarations) {
			int index = classDeclarationNames.indexOf(classDeclaration.getClassName());
			if (index > 0) {
				classDeclarationMatch = declarations.get(index);
				Annotation a = holder.createErrorAnnotation(classDeclarationMatch.getClassNameNode(), Plugin.resources.getString("lang.header.annotator.class_already_defined"));
				a.setTextAttributes(SharedSyntaxHighlighterColors.RED_WORD);
				a = holder.createErrorAnnotation(classDeclaration.getClassNameNode(), Plugin.resources.getString("lang.header.annotator.class_already_defined"));
				a.setTextAttributes(SharedSyntaxHighlighterColors.RED_WORD);
			} else {
				classDeclarationNames.add(classDeclaration.getClassName());
			}
		}
	}
}
