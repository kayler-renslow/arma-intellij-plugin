package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

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
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatization;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFParamsStatement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 @author Kayler
 Annotator used for costly annotating
 Created on 06/03/2016. */
public class SQFVisitorExternalAnnotator extends SQFVisitor {

	private AnnotationHolder annotator;

	public SQFVisitorExternalAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}

	@Override
	public void visitPsiElement(@NotNull PsiElement o) {
		PsiElement[] children = o.getChildren();
		for (PsiElement child : children) {
			child.accept(this);
		}
		super.visitPsiElement(o);
	}

	@Override
	public void visitFileScope(@NotNull SQFFileScope o) {
		super.visitFileScope(o);
	}

	@Override
	public void visitVariable(@NotNull SQFVariable var) {
		super.visitVariable(var);
		if(var.isLangVar()){
			return;
		}
		if (var.getParent() instanceof SQFMacroCall) {
			return;
		}
		boolean isGlobalVar = var.isGlobalVariable();

		PsiReference[] references;
		if (isGlobalVar) {
			references = ReferenceProvidersRegistry.getReferencesFromProviders(var);
		} else {
			references = var.getReferences();
		}
		int numAssignments = 0;
		SQFScope containingScope;
		boolean isUsedOverride = false;
		boolean isDefinedByParams = false;

		for (PsiReference reference : references) {
			PsiElement resolve = reference.resolve();
			if (resolve == null) {
				continue;
			}
			if (resolve instanceof SQFVariable) {
				SQFVariable resolveVar = (SQFVariable) resolve;
				if (resolveVar.isAssigningVariable()) {
					numAssignments++;
				}
				if(isGlobalVar){
					if(SQFStatic.followsSQFFunctionNameRules(resolveVar.getVarName())){
						isUsedOverride = true;
						numAssignments++;
						break;
					}
				}
			}
			if (!isGlobalVar) {
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
		}

		SQFPrivatization privatization = null;
		if (!isGlobalVar) {
			privatization = var.getPrivatization();
			if (privatization instanceof SQFPrivatization.SQFPrivateDeclParams) {
				SQFParamsStatement paramsStatement = ((SQFPrivatization.SQFPrivateDeclParams) privatization).getDeclarationElement();
				if (paramsStatement != null && paramsStatement.varIsDefined(var.getVarName())) {
					isDefinedByParams = true;
				}
			}
		}
		if (numAssignments == references.length && !isUsedOverride) {
			annotator.createWeakWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_unused"));
		}
		if (numAssignments == 0 && !isDefinedByParams) {
			annotator.createWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized"));
		}

		SQFScope declScope = var.getDeclarationScope();
		if (privatization != null || isGlobalVar) {
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
		int rangeStartOffset, rangeLengthOffset;
		while (iter.hasNext()) {
			n1 = iter.next().getVarElement().getNode();
			matchedIndex = vars.indexOf(n1.getText());
			if (matchedIndex >= 0) {
				n2 = declVars.get(matchedIndex).getVarElement().getNode();
				if(declVars.get(matchedIndex).getVarElement() instanceof SQFString){
					rangeStartOffset = 1;
					rangeLengthOffset = -2;
				}else{
					rangeStartOffset = 0;
					rangeLengthOffset = 0;
				}
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n1.getStartOffset() + rangeStartOffset, n1.getTextLength() + rangeLengthOffset), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n2.getStartOffset() + rangeStartOffset, n2.getTextLength() + rangeLengthOffset), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
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
					ASTNode[] statements = varScope.getNode().getChildren(TokenSet.create(SQFTypes.COMMAND_EXPRESSION));
					SQFCommandExpression commandExpression;
					SQFPrivateDecl decl;
					for (ASTNode nodeExpr : statements) {
						commandExpression = (SQFCommandExpression) nodeExpr.getPsi();
						decl = SQFPrivateDecl.parse(commandExpression);
						if (decl != null) {
							SQFCommandExpression newDecl = (SQFCommandExpression) SQFPsiUtil.createPrivateDeclFromExisting(project, decl, fixVar.getVarName());
							decl.getPrivateDeclExpression().replace(newDecl);
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
