package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFIfStatement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 03/15/2016.
 */
public class SQFAnnotator implements Annotator{

	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		element.accept(new SQFVisitor(){
			@Override
			public void visitIfStatement(@NotNull SQFIfStatement o) {
				super.visitIfStatement(o);
//				System.out.println("IFFF");
			}
		});
	}
}
