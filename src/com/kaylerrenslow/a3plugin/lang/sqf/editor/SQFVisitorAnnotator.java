package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.annotation.AnnotationHolder;

/**
 * Created by Kayler on 03/16/2016.
 */
public class SQFVisitorAnnotator extends com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor{

	private AnnotationHolder annotator;

	public SQFVisitorAnnotator(AnnotationHolder annotator) {
		this.annotator= annotator;
	}

}
