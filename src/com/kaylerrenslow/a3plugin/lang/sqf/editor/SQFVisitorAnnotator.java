package com.kaylerrenslow.a3plugin.lang.sqf.editor;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting.SQFSyntaxHighlighter;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFForLoopBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kayler
 *         AST visistor implementation for annotating with SQFAnnotator
 *         Created on 03/16/2016.
 */
public class SQFVisitorAnnotator extends SQFVisitor {

	private AnnotationHolder annotator;

	public void setAnnotator(@NotNull AnnotationHolder annotator) {
		this.annotator = annotator;
	}


	@Override
	public void visitElement(PsiElement element) {
		super.visitElement(element);
	}

	@Override
	public void visitComment(PsiComment comment) {
		super.visitComment(comment);
		String commentContent = SQFPsiUtil.getCommentContent(comment);
		if (commentContent.toLowerCase().startsWith("note")) {
			Annotation a = annotator.createAnnotation(HighlightSeverity.INFORMATION, TextRange.from(comment.getTextOffset() + 2, commentContent.length()), commentContent);
			a.setTextAttributes(SQFSyntaxHighlighter.COMMENT_NOTE);

		}
	}

	@Override
	public void visitPrivateDeclVar(@NotNull SQFPrivateDeclVar declVar) {
		super.visitPrivateDeclVar(declVar);
		if (declVar.getReferences().length == 0) {
			TextRange range = TextRange.from(declVar.getTextOffset() + 1, declVar.getTextLength() - 2);
			annotator.createWeakWarningAnnotation(range, Plugin.resources.getString("lang.sqf.annotator.variable_unused"));
		}
	}

	@Override
	public void visitLoopFor(@NotNull SQFLoopFor o) {
		visitForLoop(o);
		super.visitLoopFor(o);
	}

	@Override
	public void visitLoopForFrom(@NotNull SQFLoopForFrom o) {
		visitForLoop(o);
		super.visitLoopForFrom(o);
	}

	private void visitForLoop(@NotNull SQFForLoopBase forLoop) {
		if(forLoop.getLoopScope() != null){
			forLoop.getLoopScope().putUserData(SQFScope.KEY_ITERATION_VARS, forLoop.getIterationVariables());
		}
	}

	@Override
	public void visitVariable(@NotNull SQFVariable var) {
		super.visitVariable(var);
		if (var.getVariableType() != SQFTypes.LOCAL_VAR) {
			return;
		}
		if (var.getParent() instanceof SQFMacroCall) {
			return;
		}

		PsiReference[] references = var.getReferences();
		int numAssignments = 0;
		SQFScope containingScope;
		boolean isUsedOverride = false;
		for (PsiReference reference : references) {
			PsiElement resolve = reference.resolve();
			if (resolve == null) {
				continue;
			}
			if(resolve instanceof SQFVariable){
				SQFVariable resolveVar = (SQFVariable) resolve;
				if(resolveVar.isAssigningVariable()){
					numAssignments++;
				}
			}
			containingScope = SQFPsiUtil.getContainingScope(resolve);
			String[] iterVars = containingScope.getUserData(SQFScope.KEY_ITERATION_VARS);
			if (iterVars != null) {
				for (String iterVar : iterVars) {
					if (iterVar.equals(resolve.getText())) {
						numAssignments++;
						isUsedOverride = true;
					}
				}
			}
		}

		if (numAssignments == references.length && !isUsedOverride) {
			annotator.createWeakWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_unused"));
		}
		if (numAssignments == 0) {
			annotator.createWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized"));
		}

		SQFScope declScope = var.getDeclarationScope();
		boolean declarationNeeded = true;
		if (declScope != SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile())) {
			//local variables don't need to be declared private inside control structures or code blocks
			//https://community.bistudio.com/wiki/Variables#Local_Variables
			declarationNeeded = false;
		}
		List<SQFPrivateDeclVar> vars = declScope.getPrivateDeclaredVars();
		for (SQFPrivateDeclVar declVar : vars) {
			if (declVar.getVarName().equals(var.getVarName())) {
				return;
			}
		}
		if (!declarationNeeded) {
			return;
		}
		Annotation a = annotator.createWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_not_private"));
		a.registerFix(new SQFAnnotatorFixNotPrivate(var, declScope));
	}

	@Override
	public void visitScope(@NotNull SQFScope scope) {
		super.visitScope(scope);
		List<SQFPrivateDeclVar> declVars = scope.getPrivateDeclaredVars();
		Iterator<SQFPrivateDeclVar> iter = declVars.iterator();
		ArrayList<String> vars = new ArrayList<>();
		int matchedIndex;
		ASTNode n1, n2;
		while (iter.hasNext()) {
			n1 = iter.next().getNode();
			matchedIndex = vars.indexOf(n1.getText());
			if (matchedIndex >= 0) {
				n2 = declVars.get(matchedIndex).getNode();
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n1.getStartOffset() + 1, n1.getTextLength() - 2), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n2.getStartOffset() + 1, n2.getTextLength() - 2), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
			}
			vars.add(n1.getText());
		}
	}

	private class SQFAnnotatorFixNotPrivate extends IntentionAndQuickFixAction {
		private final SmartPsiElementPointer<SQFScope> varScopePointer;
		private final SmartPsiElementPointer<SQFVariable> fixVarPointer;

		public SQFAnnotatorFixNotPrivate(SQFVariable var, SQFScope declScope) {
			this.fixVarPointer = SmartPointerManager.getInstance(var.getProject()).createSmartPsiElementPointer(var, var.getContainingFile());
			this.varScopePointer = SmartPointerManager.getInstance(var.getProject()).createSmartPsiElementPointer(declScope, declScope.getContainingFile());
		}

		@NotNull
		@Override
		public String getName() {
			return Plugin.resources.getString("lang.sqf.annotator.variable_not_private.quick_fix");
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return "";
		}

		@Override
		public void applyFix(@NotNull Project project, PsiFile file, @Nullable Editor editor) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SQFScope varScope = varScopePointer.getElement();
					SQFVariable fixVar = fixVarPointer.getElement();
					ASTNode[] statements = varScope.getNode().getChildren(TokenSet.create(SQFTypes.STATEMENT));
					SQFStatement statement;
					SQFPrivateDecl decl;
					for (ASTNode nodeStatement : statements) {
						statement = (SQFStatement) nodeStatement.getPsi();
						if (statement.getPrivateDecl() != null) {
							decl = statement.getPrivateDecl();
							SQFPrivateDecl newDecl = SQFPsiUtil.createPrivateDeclFromExisting(project, decl, fixVar.getVarName());
							decl.replace(newDecl);
							return;
						}
					}

					PsiElement declStatement = SQFPsiUtil.createElement(project, String.format("private[\"%s\"];", fixVar.getVarName()), SQFTypes.STATEMENT);
					if (varScope.getFirstChild() != null) {
						varScope.addBefore(declStatement, varScope.getFirstChild());
					} else {
						varScope.add(declStatement);
					}
				}
			};
			WriteCommandAction.runWriteCommandAction(project, runnable);

		}
	}


}

