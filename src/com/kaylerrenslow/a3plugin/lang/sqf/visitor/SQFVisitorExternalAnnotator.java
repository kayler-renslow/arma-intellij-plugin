package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
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
	
	public final List<ToMarkAnnotation> toMarkAnnotations = new ArrayList<>();
	
	public static class ToRegisterFix {
		private final IntentionAndQuickFixAction quickFixAction;
		
		public ToRegisterFix(IntentionAndQuickFixAction quickFixAction) {
			this.quickFixAction = quickFixAction;
		}
		
		public IntentionAndQuickFixAction getQuickFixAction() {
			return quickFixAction;
		}
	}
	
	public static class ToMarkAnnotation {
		
		public ToRegisterFix myFix;
		
		public void setMyFix(ToRegisterFix myFix) {
			this.myFix = myFix;
		}
		
		public static class Annotation extends ToMarkAnnotation {
			
			private final HighlightSeverity warning;
			private final TextRange rangeCurrentNode;
			private final String string;
			
			public Annotation(HighlightSeverity warning, TextRange rangeCurrentNode, String string) {
				this.warning = warning;
				this.rangeCurrentNode = rangeCurrentNode;
				this.string = string;
			}
			
			public HighlightSeverity getWarning() {
				return warning;
			}
			
			public TextRange getRangeCurrentNode() {
				return rangeCurrentNode;
			}
			
			public String getString() {
				return string;
			}
			
		}
		
		public static class WarningAnnotation extends ToMarkAnnotation {
			private final SQFVariable var;
			private final String string;
			
			public WarningAnnotation(SQFVariable var, String string) {
				this.var = var;
				this.string = string;
			}
			
			public SQFVariable getVar() {
				return var;
			}
			
			public String getString() {
				return string;
			}
		}
		
		public static class WeakWarningAnnotation extends ToMarkAnnotation {
			
			private final SQFVariable var;
			private final String string;
			
			public WeakWarningAnnotation(SQFVariable var, String string) {
				this.var = var;
				this.string = string;
			}
			
			public SQFVariable getVar() {
				return var;
			}
			
			public String getString() {
				return string;
			}
		}
		
		public static class WeakWarningAnnotation2 extends ToMarkAnnotation {
			private final TextRange range;
			private final String string;
			
			public WeakWarningAnnotation2(TextRange range, String string) {
				this.range = range;
				this.string = string;
			}
			
			public TextRange getRange() {
				return range;
			}
			
			public String getString() {
				return string;
			}
		}
	}
	
	@Override
	public void visitPsiElement(@NotNull PsiElement o) {
		PsiElement[] children = o.getChildren();
		for (PsiElement child : children) {
			try {
				child.accept(this);
			} catch (Error e) {
				e.printStackTrace();
			}
		}
		try {
			super.visitPsiElement(o);
		} catch (Error e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void visitFileScope(@NotNull SQFFileScope o) {
		super.visitFileScope(o);
	}
	
	@Override
	public void visitVariable(@NotNull SQFVariable var) {
		super.visitVariable(var);
		if (var.isLangVar()) {
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
		
		int numUsages = 0;
		
		int numAssignments = 0;
		boolean isUsedOverride = false;
		boolean isDefinedByParams = false;
		for (PsiReference reference : references) {
			PsiPolyVariantReference polyRef = null;
			ResolveResult[] polyResults = null;
			int polyRefInd = 0;
			if (reference instanceof PsiPolyVariantReference) {
				polyRef = (PsiPolyVariantReference) reference;
				polyResults = polyRef.multiResolve(true);
			}
			PsiElement resolve;
			do {
				if (polyRef != null) {
					resolve = polyResults[polyRefInd++].getElement();
				} else {
					resolve = reference.resolve();
				}
				if (resolve == null) {
					continue;
				}
				numUsages++;
				if (resolve instanceof SQFVariable) {
					SQFVariable resolveVar = (SQFVariable) resolve;
					if (resolveVar.isAssigningVariable() && !resolveVar.getMyAssignment().getExpression().getText().equals("nil")) {
						numAssignments++;
					}
					if (isGlobalVar) {
						if (SQFStatic.followsSQFFunctionNameRules(resolveVar.getVarName())) {
							isUsedOverride = true;
							numAssignments++;
							break;
						}
					}
				}
			} while (polyRef != null && polyRefInd < polyResults.length);
			
		}
		
		SQFPrivatization privatization = null;
		if (!isGlobalVar) {
			privatization = var.getPrivatization();
			if (privatization instanceof SQFPrivatization.SQFPrivateDeclParams) {
				SQFParamsStatement paramsStatement = ((SQFPrivatization.SQFPrivateDeclParams) privatization).getPrivatizer();
				if (paramsStatement != null && paramsStatement.varIsDefined(var.getVarName())) {
					isDefinedByParams = true;
				}
			}
		}
		if (numAssignments == numUsages && !isUsedOverride) {
			toMarkAnnotations.add(new ToMarkAnnotation.WeakWarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_unused")));
		}
		if (numAssignments == 0 && !isDefinedByParams) {
			if (privatization instanceof SQFPrivatization.SQFPrivateDeclParams) {
				toMarkAnnotations.add(new ToMarkAnnotation.WarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_may_be_uninitialized")));
			} else {
				toMarkAnnotations.add(new ToMarkAnnotation.WarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized")));
			}
		}
		
		SQFScope declScope = var.getDeclarationScope();
		if (privatization != null || isGlobalVar) {
			return;
		}
		
		ToMarkAnnotation.WarningAnnotation toMark = new ToMarkAnnotation.WarningAnnotation(var, Plugin.resources.getString("lang.sqf.annotator.variable_not_private"));
		toMarkAnnotations.add(toMark);
		toMark.setMyFix(new ToRegisterFix(new SQFAnnotatorFixNotPrivate(var, declScope)));
	}
	
	@Override
	public void visitScope(@NotNull SQFScope scope) {
		super.visitScope(scope);
		List<SQFPrivateDeclVar> privateVars = scope.getPrivateVars();
		Iterator<SQFPrivateDeclVar> iter = privateVars.iterator();
		ArrayList<String> vars = new ArrayList<>();
		int matchedIndex;
		ASTNode currentPrivateVarNode, matchedNode;
		TextRange rangeCurrentNode, rangeMatchedNode;
		SQFPrivateDeclVar currentPrivateVar;
		while (iter.hasNext()) {
			currentPrivateVar = iter.next();
			currentPrivateVarNode = currentPrivateVar.getVarElement().getNode();
			matchedIndex = vars.indexOf(currentPrivateVar.getVarName());
			if (matchedIndex >= 0) {
				matchedNode = privateVars.get(matchedIndex).getVarElement().getNode();
				if (currentPrivateVarNode.getPsi() instanceof SQFString) {
					rangeCurrentNode = ((SQFString) (currentPrivateVarNode.getPsi())).getNonQuoteRangeRelativeToFile();
				} else {
					rangeCurrentNode = TextRange.from(currentPrivateVarNode.getStartOffset(), currentPrivateVarNode.getTextLength());
				}
				if (matchedNode.getPsi() instanceof SQFString) {
					rangeMatchedNode = ((SQFString) (matchedNode.getPsi())).getNonQuoteRangeRelativeToFile();
				} else {
					rangeMatchedNode = TextRange.from(matchedNode.getStartOffset(), matchedNode.getTextLength());
				}
				toMarkAnnotations.add(new ToMarkAnnotation.Annotation(HighlightSeverity.WARNING, rangeCurrentNode, Plugin.resources.getString("lang.sqf.annotator.variable_already_private")));
				toMarkAnnotations.add(new ToMarkAnnotation.Annotation(HighlightSeverity.WARNING, rangeMatchedNode, Plugin.resources.getString("lang.sqf.annotator.variable_already_private")));
			}
			if (currentPrivateVar.getVarElement() instanceof SQFString) { //usage check already handled for variables, so only need to check for strings
				if (currentPrivateVar.getVarElement().getReferences().length == 0) {
					TextRange range = ((SQFString) currentPrivateVar.getVarElement()).getNonQuoteRangeRelativeToFile();
					toMarkAnnotations.add(new ToMarkAnnotation.WeakWarningAnnotation2(range, Plugin.resources.getString("lang.sqf.annotator.variable_unused")));
				}
			}
			vars.add(currentPrivateVar.getVarName());
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
					ArrayList<SQFCommandExpression> commandExpressions = PsiUtil.findDescendantElementsOfInstance(file, SQFCommandExpression.class, null);
					SQFPrivateDecl decl;
					for (SQFCommandExpression commandExpression : commandExpressions) {
						decl = SQFPrivateDecl.parse(commandExpression);
						if (decl != null) {
							SQFCommandExpression newDecl = SQFPsiUtil.createPrivateDeclFromExisting(project, decl, fixVar.getVarName());
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
