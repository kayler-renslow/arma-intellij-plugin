package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFScope extends PsiElement {
	/**
	 * This will return all variables that are private in this scope.
	 * This doesn't guarantee that all of them were declared private in this scope!
	 *
	 * @return a set containing all private variables for the scope
	 * @see #getPrivateVarInstances(boolean)
	 */
	@NotNull
	default Set<SQFVariableName> getPrivateVars() {
		List<SQFPrivateVar> varInstances = getPrivateVarInstances(true);
		Set<SQFVariableName> vars = new HashSet<>(varInstances.size());
		for (SQFPrivateVar var : varInstances) {
			vars.add(var.getVariableNameObj());
		}
		return vars;
	}

	/**
	 * This will return all variables that are private in this scope.
	 * This doesn't guarantee that all of them were declared private in this scope!
	 * <p>
	 * The order of the list will be: the private vars in the current scope will appear first and then private vars
	 * will be appended in the order that they are written in text
	 *
	 * @param checkContainingScope if true, the results will include private vars that are passed in from this scope's
	 *                             containing scope
	 * @return a set containing all private variables for the scope and places them in {@link SQFPrivateVar} instances
	 */
	@NotNull
	default List<SQFPrivateVar> getPrivateVarInstances(boolean checkContainingScope) {
		List<SQFPrivateVar> vars = new ArrayList<>();

		Function<SQFScope, Void> collectPrivateVarsFromScope = sqfScope -> {
			for (PsiElement element : sqfScope.getChildren()) {
				if (!(element instanceof SQFStatement)) {
					continue;
				}
				SQFStatement statement = (SQFStatement) element;
				List<SQFPrivateVar> declaredPrivateVars = statement.getDeclaredPrivateVars();
				vars.addAll(declaredPrivateVars);
			}

			return null;
		};

		if (checkContainingScope) {
			SQFScope fileScope = getContainingScope(this.getContainingFile());

			//do this before we compare against file scope's private vars
			collectPrivateVarsFromScope.apply(this);

			for (SQFPrivateVar declaredPrivateVar : fileScope.getPrivateVarInstances(false)) {
				//copy over private vars
				boolean add = true;
				for (SQFPrivateVar myPrivateVar : vars) {
					if (declaredPrivateVar.getVariableNameObj().equals(myPrivateVar.getVariableNameObj())
							&& declaredPrivateVar.getMaxScope() == myPrivateVar.getMaxScope()) {
						//if declared private in this scope, we don't want the parent scope's private vars that match
						add = false;
						break;
					}
					if (declaredPrivateVar.getVarNameElement().getTextOffset() > this.getTextOffset()) {
						//only private vars coming before this scope in the parent scope should be available in this scope
						add = false;
						break;
					}
				}
				if (add) {
					vars.add(declaredPrivateVar);
				}
			}
		} else {
			collectPrivateVarsFromScope.apply(this);
		}

		return vars;
	}

	@NotNull
	static List<SQFVariableReference> getVariableReferencesFor(@NotNull SQFVariable variable) {
		List<SQFVariableReference> vars = new ArrayList<>();
		PsiFile file = variable.getContainingFile();
		if (file == null) {
			throw new IllegalArgumentException("variable doesn't have a containing file");
		}
		SQFVariableName variableNameObj = variable.getVarNameObj();
		SQFScope variableMaxScope = null; //this is the scope that the variable exists in
		SQFScope containingScope = getContainingScope(variable);
		TextRange containingScopeRange = containingScope.getTextRange();
		List<SQFScope> ignoreScopes = new LinkedList<>(); //scopes to not get references from

		if (variable.isLocal() && !variable.isMagicVar()) {
			List<SQFPrivateVar> privateVarInstances = containingScope.getPrivateVarInstances(true);
			for (SQFPrivateVar privateVar : privateVarInstances) {
				ignoreScopes.add(privateVar.getMaxScope());
			}

			for (SQFPrivateVar privateVar : privateVarInstances) {
				if (!privateVar.getVariableNameObj().equals(variableNameObj)) {
					continue;
				}
				SQFScope possibleMaxScope = privateVar.getMaxScope();
				TextRange possibleMaxScopeRange = possibleMaxScope.getTextRange();
				if (containingScopeRange.contains(possibleMaxScopeRange)) {
					//possibleMaxScope is deeper than the containing scope
					continue;
				}
				if (possibleMaxScopeRange.getStartOffset() > containingScopeRange.getStartOffset()) {
					//possibleMaxScope comes after the containing scope
					continue;
				}
				if (possibleMaxScope != containingScope && !possibleMaxScopeRange.contains(containingScopeRange)) {
					//max scope isn't the containing scope and the possibleMaxScope doesn't have containingScope in it,
					//so the private var isn't relevant
					continue;
				}
				if (possibleMaxScopeRange.getStartOffset() > variable.getTextOffset()) {
					//possibleMaxScope is coming after the variable, so ignore it
					continue;
				}

				if (variableMaxScope == null) {
					variableMaxScope = possibleMaxScope;
					ignoreScopes.remove(possibleMaxScope);
				} else {
					//if we already have a variableMaxScope defined, we want to make sure that the possibleMaxScope is
					//deeper than the current variableMaxScope in order to update variableMaxScope to possibleMaxScope

					//todo FIX THIS
					if (variableMaxScope.getTextRange().contains(possibleMaxScopeRange)
							|| possibleMaxScope == containingScope) {
						variableMaxScope = possibleMaxScope;
						ignoreScopes.remove(possibleMaxScope);
					}
				}
			}

			if (variableMaxScope == null /*not declared private*/) {
				variableMaxScope = SQFScope.getContainingScope(file);
			}
		} else if (variable.isMagicVar()) {
			variableMaxScope = SQFScope.getContainingScope(variable);
		} else {
			//global var
			variableMaxScope = SQFScope.getContainingScope(file);
		}

		ignoreScopes.remove(variableMaxScope);

		List<SQFVariable> varTargets = new ArrayList<>();
		List<SQFString> stringTargets = new ArrayList<>();
		for (PsiElement element : variableMaxScope.getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			SQFStatement statement = (SQFStatement) element;
			List<SQFPrivateVar> statementDeclaredPrivateVars = statement.getDeclaredPrivateVars();

			SQFScope finalVariableMaxScope = variableMaxScope;
			PsiUtil.traverseInLayers(statement.getNode(), astNode -> {
				PsiElement nodeAsPsi = astNode.getPsi();
				if (nodeAsPsi instanceof SQFScope) {
					SQFScope scope = (SQFScope) nodeAsPsi;
					if (ignoreScopes.contains(scope)) {
						return true;
					}
				}

				if (!(nodeAsPsi instanceof SQFVariable || nodeAsPsi instanceof SQFString)) {
					return false;
				}
				SQFVariable nameEqualVar = null;
				SQFString nameEqualStr = null;
				if (nodeAsPsi instanceof SQFVariable) {
					SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
					if (SQFVariableName.nameEquals(sqfVariable.getVarName(), variable.getVarName())) {
						nameEqualVar = sqfVariable;
					} else {
						return false;
					}
				} else {
					SQFString string = (SQFString) nodeAsPsi;
					if (SQFVariableName.nameEquals(string.getNonQuoteText(), variable.getVarName())) {
						nameEqualStr = string;
					} else {
						return false;
					}
				}

				boolean differentScope = false;
				if (variable.isLocal()) {

					//no need to do the following code if the variable is global
					for (SQFPrivateVar statementDeclaredPrivateVar : statementDeclaredPrivateVars) {
						if (statementDeclaredPrivateVar.getVariableNameObj().equals(variableNameObj)) {
							if (statementDeclaredPrivateVar.getMaxScope() != finalVariableMaxScope) {
								//if the variable is made private in any descendant scopes, we don't want to reference those
								//because the max scopes don't match
								differentScope = true;
								break;
							}
						}
					}
				}

				if (!differentScope) {
					if (nameEqualVar != null) {
						varTargets.add(nameEqualVar);
					} else {
						stringTargets.add(nameEqualStr);
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
	static SQFScope getContainingScope(@NotNull PsiElement element) {
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

	@NotNull
	default String getTextNoNewlines() {
		return getText().replaceAll("\n", " ");
	}
}
