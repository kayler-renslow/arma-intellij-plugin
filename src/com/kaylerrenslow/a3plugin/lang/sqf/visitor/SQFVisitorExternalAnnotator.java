package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFFileScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 06/03/2016.
 */
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
		//		if (var.getVariableType() != SQFTypes.LOCAL_VAR) {
		//			return;
		//		}
		//		if (var.getParent() instanceof SQFMacroCall) {
		//			return;
		//		}
		//
		//		PsiReference[] references = var.getReferences();
		//		int numAssignments = 0;
		//		SQFScope containingScope;
		//		boolean isUsedOverride = false;
		//		boolean isDefinedByParams = false;
		//
		//		for (PsiReference reference : references) {
		//			PsiElement resolve = reference.resolve();
		//			if (resolve == null) {
		//				continue;
		//			}
		//			if(resolve instanceof SQFVariable){
		//				SQFVariable resolveVar = (SQFVariable) resolve;
		//				if(resolveVar.isAssigningVariable()){
		//					numAssignments++;
		//				}
		//			}
		//			containingScope = SQFPsiUtil.getContainingScope(resolve);
		//			String[] iterVars = containingScope.getUserData(SQFScope.KEY_ITERATION_VARS);
		//			if (iterVars != null) {
		//				for (String iterVar : iterVars) {
		//					if (iterVar.equals(resolve.getText())) {
		//						numAssignments++;
		//						isUsedOverride = true;
		//					}
		//				}
		//			}
		//		}
		//		SQFPrivatization privatization = var.getPrivatization();
		//		if(privatization instanceof SQFPrivatization.SQFPrivateDeclParams){
		//			SQFParamsStatement paramsStatement = ((SQFPrivatization.SQFPrivateDeclParams)privatization).getDeclarationElement();
		//			if(paramsStatement.varIsDefined(var.getVarName())){
		//				isDefinedByParams = true;
		//			}
		//		}
		//		if (numAssignments == references.length && !isUsedOverride) {
		//			annotator.createWeakWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_unused"));
		//		}
		//		if (numAssignments == 0 && !isDefinedByParams) {
		//			annotator.createWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized"));
		//		}
		//
		//		SQFScope declScope = var.getDeclarationScope();
		//		if (privatization != null) {
		//			return;
		//		}
		//		Annotation a = annotator.createWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_not_private"));
		//		a.registerFix(new SQFAnnotatorFixNotPrivate(var, declScope));
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
			//			Runnable runnable = new Runnable() {
			//				@Override
			//				public void run() {
			//					SQFScope varScope = varScopePointer.getElement();
			//					SQFVariable fixVar = fixVarPointer.getElement();
			//					ASTNode[] statements = varScope.getNode().getChildren(TokenSet.create(SQFTypes.STATEMENT));
			//					SQFStatement statement;
			//					SQFPrivateDecl decl;
			//					for (ASTNode nodeStatement : statements) {
			//						statement = (SQFStatement) nodeStatement.getPsi();
			//						if (statement.getPrivateDecl() != null) {
			//							decl = statement.getPrivateDecl();
			//							SQFPrivateDecl newDecl = SQFPsiUtil.createPrivateDeclFromExisting(project, decl, fixVar.getVarName());
			//							decl.replace(newDecl);
			//							return;
			//						}
			//					}
			//
			//					PsiElement declStatement = SQFPsiUtil.createElement(project, String.format("private[\"%s\"];", fixVar.getVarName()), SQFTypes.STATEMENT);
			//					if (varScope.getFirstChild() != null) {
			//						varScope.addBefore(declStatement, varScope.getFirstChild());
			//					} else {
			//						varScope.add(declStatement);
			//					}
			//				}
			//			};
			//			WriteCommandAction.runWriteCommandAction(project, runnable);

		}
	}


}
