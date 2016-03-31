package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPrivateDeclVar;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kayler
 * AST visistor implementation for annotating with SQFAnnotator
 * Created on 03/16/2016.
 */
public class SQFVisitorAnnotator extends com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor{

	private AnnotationHolder annotator;

	private static final String ALREADY_PRIVATE = Plugin.resources.getString("lang.sqf.annotator.variable_already_private") ;

	public SQFVisitorAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}


//	@Override
//	public void visitPsiElement(@NotNull PsiElement o) {
//		System.out.println(o.getClass());
//		if(o instanceof PsiErrorElement){
//			System.out.println(((PsiErrorElement) o).getErrorDescription());
//			PsiErrorElement errorElement = (PsiErrorElement)o;
//			Annotation a = annotator.createErrorAnnotation(errorElement.getContainingFile(), errorElement.getErrorDescription());
//			a.setFileLevelAnnotation(true);
//		}
//		super.visitPsiElement(o);
//	}


	@Override
	public void visitElement(PsiElement element) {
		super.visitElement(element);
	}

	@Override
	public void visitComment(PsiComment comment) {
		super.visitComment(comment);
		String commentContent = SQFPsiUtil.getCommentContent(comment);
		if(commentContent.toLowerCase().startsWith("note")){
			Annotation a= annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.from(comment.getTextOffset() + 2, commentContent.length()), commentContent);
			a.setTextAttributes(SQFSyntaxHighlighter.COMMENT_NOTE);

		}
	}


	@Override
	public void visitPrivateDecl(@NotNull SQFPrivateDecl privateDecl) {
		super.visitPrivateDecl(privateDecl);
		List<SQFPrivateDeclVar> varList = privateDecl.getPrivateDeclVarList();
		Iterator<SQFPrivateDeclVar> iter = varList.iterator();
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
