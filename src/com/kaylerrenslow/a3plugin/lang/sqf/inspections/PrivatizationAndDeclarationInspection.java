package com.kaylerrenslow.a3plugin.lang.sqf.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.lang.ASTNode;
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
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 Created by Kayler on 08/03/2016.
 */
public class PrivatizationAndDeclarationInspection extends LocalInspectionTool {
	
	
	@NotNull
	@Override
	public HighlightDisplayLevel getDefaultLevel() {
		return HighlightDisplayLevel.WARNING;
	}
	
	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkFile(file, manager, isOnTheFly);
	}
	
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
		return new InspectionVisitor(holder);
	}
	
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return super.buildVisitor(holder, isOnTheFly);
	}
	
	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return Plugin.resources.getString("lang.sqf.inspection.privatization_and_declaration.display_name");
	}
		
	@Nls
	@NotNull
	@Override
	public String getGroupDisplayName() {
		return Plugin.resources.getString("lang.sqf.inspection.group.misc.group_display_name");
	}
	
	/**
	 @author Kayler
	 Used for making sure variables are declared private, aren't declared private more than once, and are defined.
	 Created on 06/03/2016.
	 */
	private static class InspectionVisitor extends SQFVisitor {
		
		private ProblemsHolder holder;
		
		InspectionVisitor(ProblemsHolder holder) {
			this.holder = holder;
		}
		
		@Override
		public void visitVariable(@NotNull SQFVariable var) {
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
				holder.registerProblem(var, Plugin.resources.getString("lang.sqf.annotator.variable_unused"), ProblemHighlightType.WEAK_WARNING);
			}
			if (numAssignments == 0 && !isDefinedByParams) {
				if (privatization instanceof SQFPrivatization.SQFPrivateDeclParams) {
					holder.registerProblem(var, Plugin.resources.getString("lang.sqf.annotator.variable_may_be_uninitialized"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
				} else {
					holder.registerProblem(var, Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
				}
			}
			
			SQFScope declScope = var.getDeclarationScope();
			if (privatization != null || isGlobalVar) {
				return;
			}
			holder.registerProblem(var, Plugin.resources.getString("lang.sqf.annotator.variable_not_private"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new SQFAnnotatorFixNotPrivate(var, declScope));
			
		}
		
		@Override
		public void visitScope(@NotNull SQFScope scope) {
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
					holder.registerProblem(matchedNode.getPsi(), rangeMatchedNode, Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
					holder.registerProblem(matchedNode.getPsi(), rangeCurrentNode, Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
				}
				if (currentPrivateVar.getVarElement() instanceof SQFString) { //usage check already handled for variables, so only need to check for strings
					if (currentPrivateVar.getVarElement().getReferences().length == 0) {
						TextRange range = ((SQFString) currentPrivateVar.getVarElement()).getNonQuoteRangeRelativeToFile();
						holder.registerProblem(currentPrivateVar.getVarElement(), range, Plugin.resources.getString("lang.sqf.annotator.variable_unused"));
					}
				}
				vars.add(currentPrivateVar.getVarName());
			}
		}
		
		private class SQFAnnotatorFixNotPrivate extends IntentionAndQuickFixAction {
			private final SmartPsiElementPointer<SQFScope> varScopePointer;
			private final SmartPsiElementPointer<SQFVariable> fixVarPointer;
			
			SQFAnnotatorFixNotPrivate(SQFVariable var, SQFScope declScope) {
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
						if(fixVar == null || varScope == null){
							return;
						}
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
	
}
