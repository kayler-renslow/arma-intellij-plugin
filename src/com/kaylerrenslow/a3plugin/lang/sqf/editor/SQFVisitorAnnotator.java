package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariableAsString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		List<SQFVariableAsString> varList = o.getVariableAsStringList();
		Iterator<SQFVariableAsString> iter = varList.iterator();
		ArrayList<String> vars = new ArrayList<>();
		int matchedIndex;
		ASTNode n1, n2;
		while (iter.hasNext()){
			n1 = iter.next().getNode();
			matchedIndex = vars.indexOf(n1.getText());
			if(matchedIndex >=0 ){
				n2 = varList.get(matchedIndex).getNode();
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n1.getStartOffset() + 1, n1.getTextLength() - 2), ALREADY_PRIVATE);
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n2.getStartOffset() + 1, n2.getTextLength() - 2), ALREADY_PRIVATE);
			}
			vars.add(n1.getText());

		}

	}
}
