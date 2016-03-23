package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDeclVar;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kayler on 03/16/2016.
 */
public class SQFVisitorAnnotator extends com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor{

	private AnnotationHolder annotator;

	private static final String ALREADY_PRIVATE = Plugin.resources.getString("lang.sqf.annotator.variable_already_private") ;

	public SQFVisitorAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}

	@Override
	public void visitPrivateDecl(@NotNull SQFPrivateDecl o) {
		super.visitPrivateDecl(o);
		Iterator<SQFPrivateDeclVar> iter = o.getPrivateDeclVarList().iterator();
		ArrayList<String> vars = new ArrayList<>();
		while (iter.hasNext()){
			ASTNode n = iter.next().getNode();
			if(vars.contains(n.getText())){
				Annotation a = annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n.getStartOffset() + 1, n.getTextLength() - 2), ALREADY_PRIVATE);
			}
			vars.add(n.getText());

		}

	}
}
