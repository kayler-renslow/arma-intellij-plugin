package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 Created by Kayler on 06/03/2016.
 */
public class SQFVisitorExternalAnnotator extends SQFVisitor {

	private AnnotationHolder annotator;
	private Stack<SQFEnv> envStack = new Stack<>();
	private SQFEnv currentEnv;

	private Annotation expect(String expected, PsiElement got) {
		return annotator.createErrorAnnotation(got, String.format(Plugin.resources.getString("lang.sqf.annotator.error.expected_f"), expected, got.getText()));
	}

	private Annotation createDeleteTokenAnotation(PsiElement element) {
		return annotator.createErrorAnnotation(element, String.format(Plugin.resources.getString("lang.sqf.annotator.error.unexpected_f"), element.getText()));
	}

	public SQFVisitorExternalAnnotator(AnnotationHolder annotator) {
		this.annotator = annotator;
	}

	@Override
	public void visitFileScope(@NotNull SQFFileScope o) {
		System.out.println("SQFVisitorExternalAnnotator.visitFileScope");
		envStack.add(new SQFEnv(o));
		currentEnv = envStack.peek();
		super.visitFileScope(o);
	}

	@Override
	public void visitAssignment(@NotNull SQFAssignment o) {
		SQFVariable assignVar = o.getAssigningVariable();
		if (o.getCommand() != null) {
			if (o.getCommand().getText().equals("private")) {
				currentEnv.privatize(assignVar.getVarName(), assignVar);
			}
		}
		currentEnv.initialize(assignVar.getVarName());
		super.visitAssignment(o);
	}

	@Override
	public void visitVariable(@NotNull SQFVariable var) {
		currentEnv.addUsage(var);
		System.out.println("SQFVisitorExternalAnnotator.visitVariable");
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

	@Override
	public void visitCommandExpression(@NotNull SQFCommandExpression o) {
		PsiElement prefix = o.getPrefixArgument();
		PsiElement postfix = o.getPostfixArgument();
		String commandName = o.getCommand().getText();
		if (commandName.equals("private")) {
			boolean error = false;
			if (postfix instanceof SQFExpression && postfix.getFirstChild() instanceof SQFLiteralExpression) {
				SQFExpression expression = (SQFExpression) postfix;
				SQFLiteralExpression literal = (SQFLiteralExpression) expression.getFirstChild();
				if (literal.getArrayVal() != null) {
					SQFArrayVal array = literal.getArrayVal();
					ArrayList<ASTNode> strings = PsiUtil.findDescendantElements(array, SQFTypes.STRING, array.getNode());
					SQFString sqfString;
					for (ASTNode string : strings) {
						sqfString = (SQFString) string;
						currentEnv.privatize(sqfString.getNonQuoteText(), string.getPsi());
					}
				} else if (literal.getString() != null) {
					SQFString string = literal.getString();
					currentEnv.privatize(string.getNonQuoteText(), string);
				} else {
					error = true;
				}
			} else {
				error = true;
			}
			if (error) {
				if (prefix != null) {
					createDeleteTokenAnotation(prefix);
				}
				expect("String or Array of Strings", postfix);
			}
		} else if (commandName.equals("params")) {

		}
		super.visitCommandExpression(o);
	}

	@Override
	public void visitScope(@NotNull SQFScope scope) {
		envStack.add(new SQFEnv(scope));
		currentEnv = envStack.peek();
		super.visitScope(scope);
		SQFEnv myEnv = envStack.pop();

		List<SQFEnvVar> envVars = myEnv.getVars();
		for (SQFEnvVar envVar : envVars) {
			if (envVar.isPrivate() && envVar.getUsages() == null) {
				String warning = Plugin.resources.getString("lang.sqf.annotator.variable_unused");
				int start;
				boolean alreadyPrivate = envVar.getPrivatizers().size() > 1;
				String warningAlreadyPrivate = Plugin.resources.getString("lang.sqf.annotator.variable_already_private");
				for (PsiElement privatizer : envVar.getPrivatizers()) {
					start = privatizer.getTextOffset();
					if (privatizer instanceof SQFString) {
						annotator.createWeakWarningAnnotation(TextRange.from(start + 1, privatizer.getTextLength() - 1), warning);
					} else {
						annotator.createWeakWarningAnnotation(privatizer, warning);
					}
					if (alreadyPrivate) {
						annotator.createWeakWarningAnnotation(privatizer, warningAlreadyPrivate);
					}
				}
				continue;
			}
			if (!envVar.isInitialized() && envVar.getUsages() != null) {
				String warning = Plugin.resources.getString("lang.sqf.annotator.variable_uninitialized");
				for (SQFVariable usage : envVar.getUsages()) {
					annotator.createWarningAnnotation(usage, warning);
				}

			}
			if (!envVar.isPrivate() && envVar.getUsages() != null) {
				String warning = Plugin.resources.getString("lang.sqf.annotator.variable_not_private");
				for (SQFVariable usage : envVar.getUsages()) {
					annotator.createWarningAnnotation(usage, warning);
				}
			}
			System.out.println(envVar.getVarName());

		}
		//		List<SQFPrivateDeclVar> declVars = scope.getPrivateDeclaredVars();
		//		Iterator<SQFPrivateDeclVar> iter = declVars.iterator();
		//		ArrayList<String> vars = new ArrayList<>();
		//		int matchedIndex;
		//		ASTNode n1, n2;
		//		while (iter.hasNext()) {
		//			n1 = iter.next().getNode();
		//			matchedIndex = vars.indexOf(n1.getText());
		//			if (matchedIndex >= 0) {
		//				n2 = declVars.get(matchedIndex).getNode();
		//				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n1.getStartOffset() + 1, n1.getTextLength() - 2), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
		//				annotator.createAnnotation(HighlightSeverity.WARNING, TextRange.from(n2.getStartOffset() + 1, n2.getTextLength() - 2), Plugin.resources.getString("lang.sqf.annotator.variable_already_private"));
		//			}
		//			vars.add(n1.getText());
		//		}
		if (!envStack.isEmpty()) {
			currentEnv = envStack.peek();
		} else {
			currentEnv = null;
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
