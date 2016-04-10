package com.kaylerrenslow.a3plugin.lang.header.editor;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.codeStyle.highlighting.HeaderSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.header.psi.*;
import com.kaylerrenslow.a3plugin.lang.shared.SharedSyntaxHighlighterColors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kayler
 *         Annotator for Header language
 *         Created on 04/08/2016.
 */
public class HeaderAnnotatorVisitor extends HeaderVisitor {

	private AnnotationHolder holder;

	private String[] preprocessors = {"#define", "#undef", "#ifdef", "#ifndef", "#else", "#endif"};

	public void setAnnotationHolder(AnnotationHolder holder) {
		this.holder = holder;
	}

	@Override
	public void visitPreDefine(@NotNull HeaderPreDefine define) {
		super.visitPreDefine(define);
		annotatePreprocessorText(define);
	}

	@Override
	public void visitPreIfdef(@NotNull HeaderPreIfdef ifdef) {
		super.visitPreIfdef(ifdef);
		annotatePreprocessorText(ifdef);
	}

	@Override
	public void visitPreIfndef(@NotNull HeaderPreIfndef ifndef) {
		super.visitPreIfndef(ifndef);
		annotatePreprocessorText(ifndef);
	}


	@Override
	public void visitPreUndef(@NotNull HeaderPreUndef undef) {
		super.visitPreUndef(undef);
		annotatePreprocessorText(undef);
	}

	private void annotatePreprocessorText(PsiElement preprocessorElement) {
		String text = preprocessorElement.getText();
		String[] tokens = text.split("\\s");
		int offset = preprocessorElement.getTextOffset();
		for (String s : tokens) {
			for (String preprocessor : preprocessors) {
				if (s.equals(preprocessor)) {
					Annotation a = holder.createInfoAnnotation(TextRange.from(offset + text.indexOf(s), s.length()), null);
					a.setTextAttributes(HeaderSyntaxHighlighter.PREPROCESSOR);
				}
			}
		}
	}

	@Override
	public void visitFileEntries(@NotNull HeaderFileEntries entries) {
		super.visitFileEntries(entries);
		ArrayList<HeaderClassDeclaration> declarations = HeaderPsiUtil.getClassDeclarationsWithEntriesEqual(entries, null, null, false, 1, 1);

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
