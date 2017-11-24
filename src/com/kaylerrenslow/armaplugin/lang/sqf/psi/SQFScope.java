package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public abstract class SQFScope extends ASTWrapperPsiElement implements SQFSyntaxNode {

	public SQFScope(@NotNull ASTNode node) {
		super(node);
	}


	/**
	 * This will return all variables that are private in this scope. Note that this may return private vars
	 * that are not declared private in this scope.
	 *
	 * @return a set containing all private variables for the scope's file
	 * @see #searchForPrivateVarInstances(boolean, SQFScope.VariableScopeHelper)
	 */
	@NotNull
	public Set<SQFVariableName> getPrivateVars() {
		VariableScopeHelper helper = new VariableScopeHelper();
		searchForPrivateVarInstances(true, helper);
		Set<SQFPrivateVar> privateVarsForScope = helper.getPrivateVarsForScope(this);
		Set<SQFVariableName> vars = new HashSet<>(privateVarsForScope.size());
		for (SQFPrivateVar var : privateVarsForScope) {
			vars.add(var.getVariableNameObj());
		}
		return vars;
	}

	/**
	 * @return a list of {@link SQFStatement} instances that are direct children of this scope
	 */
	@NotNull
	public List<SQFStatement> getChildStatements() {
		List<SQFStatement> statements = new ArrayList<>();
		for (PsiElement element : getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			SQFStatement statement = (SQFStatement) element;
			statements.add(statement);
		}
		return statements;
	}

	@NotNull
	public String getTextNoNewlines() {
		return getText().replaceAll("\n", " ");
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}

	/**
	 * This will return all variables that are private in this scope or the file's scope.
	 *
	 * @param checkFileScope if true, will get all private vars for the entire SQF file. If false, will only get them
	 *                       for this {@link SQFScope} instance
	 */
	public void searchForPrivateVarInstances(boolean checkFileScope, @NotNull VariableScopeHelper scopeHelper) {

		Function<SQFScope, Void> collectPrivateVarsFromScope = sqfScope -> {
			for (PsiElement element : sqfScope.getChildren()) {
				if (!(element instanceof SQFStatement)) {
					continue;
				}
				SQFStatement statement = (SQFStatement) element;
				statement.searchForDeclaredPrivateVars(scopeHelper);
			}

			return null;
		};

		if (checkFileScope) {
			collectPrivateVarsFromScope.apply(getContainingScope(this.getContainingFile()));
		} else {
			collectPrivateVarsFromScope.apply(this);
		}
	}

	@NotNull
	public static List<SQFVariableReference> getVariableReferencesFor(@NotNull SQFVariable variable) {
		List<SQFVariableReference> vars = new ArrayList<>();
		PsiFile file = variable.getContainingFile();
		if (file == null) {
			throw new IllegalArgumentException("variable doesn't have a containing file");
		}
		SQFScope containingScope = getContainingScope(variable);

		VariableScopeHelper helper = new VariableScopeHelper();
		containingScope.searchForPrivateVarInstances(true, helper);

		SQFScope[] visitScopes;
		SQFScope maxScope;

		if (!variable.isLocal()) {
			maxScope = SQFScope.getContainingScope(variable.getContainingFile());
			visitScopes = new SQFScope[]{maxScope};
		} else if (variable.isMagicVar()) {
			maxScope = SQFScope.getContainingScope(variable);
			visitScopes = new SQFScope[]{maxScope};
		} else {
			SQFPrivateVar privateVar = helper.getPrivateVarForVar(variable);
			if (privateVar == null) {
				maxScope = SQFScope.getContainingScope(variable.getContainingFile());
				visitScopes = new SQFScope[]{maxScope};
			} else {
				maxScope = privateVar.getMaxScope();
				visitScopes = new SQFScope[1 + privateVar.getMergeScopes().size()];
				visitScopes[0] = privateVar.getMaxScope();
				for (int i = 1; i < visitScopes.length; i++) {
					visitScopes[i] = privateVar.getMergeScopes().get(i - 1);
				}
			}
		}


		List<SQFVariable> varTargets = new ArrayList<>();
		List<SQFString> stringTargets = new ArrayList<>();

		for (SQFScope scope : visitScopes) {
			PsiUtil.traverseBreadthFirstSearch(scope.getNode(), astNode -> {
				PsiElement nodeAsPsi = astNode.getPsi();
				if (!(nodeAsPsi instanceof SQFVariable || nodeAsPsi instanceof SQFString)) {
					return false;
				}
				if (nodeAsPsi instanceof SQFVariable) {
					SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
					if (SQFVariableName.nameEquals(sqfVariable.getVarName(), variable.getVarName())) {
						if (variable.isLocal()) {
							SQFPrivateVar privateVar = helper.getPrivateVarForVar(variable);
							if (privateVar == null || privateVar.getMaxScope() == maxScope) {
								varTargets.add(sqfVariable);
							}
						} else {
							varTargets.add(sqfVariable);
						}
					} else {
						return false;
					}
				} else {
					SQFString string = (SQFString) nodeAsPsi;
					if (SQFVariableName.nameEquals(string.getNonQuoteText(), variable.getVarName())) {
						stringTargets.add(string);
					} else {
						return false;
					}
				}

				return false;
			});
		}

		if (!varTargets.isEmpty()) {
			vars.add(new SQFVariableReference.IdentifierReference(variable, varTargets));
		}
		if (!stringTargets.isEmpty()) {
			vars.add(new SQFVariableReference.StringReference(variable, stringTargets));
		}
		return vars;
	}

	/**
	 * Gets the {@link SQFScope} for the given PsiElement. If the given element is an {@link SQFScope} instance,
	 * the first scope containing that scope will be returned.
	 * <p>
	 * If element is {@link SQFFileScope}, element will be returned and cast to {@link SQFScope};
	 * <p>
	 * If element is an {@link SQFFile}, the file's {@link SQFFileScope} will be returned
	 *
	 * @param element where to get the containing scope from
	 * @return the containing scope
	 * @throws IllegalArgumentException when element isn't in an {@link SQFFile}
	 */
	@NotNull
	public static SQFScope getContainingScope(@NotNull PsiElement element) {
		if (element instanceof SQFFile) {
			SQFFileScope fileScope = ((SQFFile) element).findChildByClass(SQFFileScope.class);
			if (fileScope == null) {
				throw new IllegalStateException("no SQFFileScope for file " + element);
			}
			return fileScope;
		}
		if (element instanceof PsiFile) {
			throw new IllegalArgumentException("element is a PsiFile, but not an SQFFile");
		}
		if (element instanceof SQFFileScope) {
			return (SQFScope) element;
		}
		PsiFile file = element.getContainingFile();
		if (!(file instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQF file");
		}
		PsiElement cursor = element;
		while (cursor != null) {
			cursor = cursor.getParent();
			if (cursor instanceof SQFScope) { //will also cover file scope
				return (SQFScope) cursor;
			}
		}
		SQFFileScope fileScope = ((SQFFile) file).findChildByClass(SQFFileScope.class);
		if (fileScope == null) {
			throw new IllegalStateException("no SQFFileScope for file " + file);
		}
		return fileScope;
	}

	static class VariableScopeHelper {
		private final Map<SQFScope, Set<SQFPrivateVar>> privateVarsScopeMap = new HashMap<>();

		/**
		 * @return the mutable set containing all {@link SQFPrivateVar} instances for the given scope
		 */
		@NotNull
		public Set<SQFPrivateVar> getPrivateVarsForScope(@NotNull SQFScope scope) {
			return privateVarsScopeMap.computeIfAbsent(scope, scope1 -> new HashSet<>());
		}

		@Nullable
		public SQFPrivateVar getPrivateVarForVar(@NotNull SQFVariable variable) {
			final String varName = variable.getVarName();
			SQFStatement statement = SQFStatement.getStatementForElement(variable);
			if (statement == null) {
				throw new IllegalStateException("variable isn't in a statement!");
			}
			SQFForLoopHelperStatement forLoopStatement = statement.getForLoopStatement();
			if (forLoopStatement != null) {
				throw new UnsupportedOperationException("we need to check if the variable is declared private in for loop (for [private _i=0...])");
			}
			SQFScope containingScope = SQFScope.getContainingScope(variable);
			while (true) {
				Set<SQFPrivateVar> set = privateVarsScopeMap.get(containingScope);
				if (set != null) {
					for (SQFPrivateVar privateVar : set) {
						if (privateVar.getVariableNameObj().nameEquals(varName)) {
							return privateVar;
						}
					}
				}
				if (containingScope instanceof SQFFileScope) {
					return null;
				}
				containingScope = SQFScope.getContainingScope(containingScope);
			}
		}

	}
}
