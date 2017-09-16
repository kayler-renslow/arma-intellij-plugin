package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
				if (declaredPrivateVars == null) {
					continue;
				}
				for (SQFPrivateVar privateVar : declaredPrivateVars) {
					if (privateVar.getMaxScope() == sqfScope) {
						vars.add(privateVar);
					}
				/* Logic of this loop:
				*  If the variable is declared private in a statement, it may be declared private in another scope that isn't this scope.
				*  However, that doesn't mean that the variable is guaranteed to be private in that scope.
				*  Example would be "for[{private _i=0;},{},{}]do {}"; the _i variable is declared private in the first scope
				*  of the array, but it's max scope is actually the scope/code block after the "do".
				*
				*  Also, for any descendant code blocks that have their own scopes, we want to exclude the variables declared private
				*  in them, unless their max scope is this scope.
				*/
				}
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
					if (declaredPrivateVar.getVariableNameObj().equals(myPrivateVar.getVariableNameObj())) {
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
		SQFScope variableMaxScope = null;
		if (variable.isLocal() && !variable.isMagicVar()) {
			for (SQFPrivateVar privateVar : getContainingScope(variable).getPrivateVarInstances(true)) {
				if (!privateVar.getVariableNameObj().equals(variableNameObj)) {
					continue;
				}
				variableMaxScope = privateVar.getMaxScope(); //this is the scope that the variable exists in
				break;
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
		List<SQFVariable> varTargets = new ArrayList<>();
		List<SQFString> stringTargets = new ArrayList<>();
		for (PsiElement element : variableMaxScope.getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			SQFStatement statement = (SQFStatement) element;
			List<SQFPrivateVar> statementDeclaredPrivateVars = statement.getDeclaredPrivateVars();

			SQFScope finalVariableMaxScope = variableMaxScope;
			PsiUtil.traverseBreadthFirstSearch(statement.getNode(), astNode -> {
				PsiElement nodeAsPsi = astNode.getPsi();
				if (!(nodeAsPsi instanceof SQFVariable || nodeAsPsi instanceof SQFString)) {
					return false;
				}
				SQFVariable varToAdd = null;
				SQFString strToAdd = null;
				if (nodeAsPsi instanceof SQFVariable) {
					SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
					if (SQFVariableName.nameEquals(sqfVariable.getVarName(), variable.getVarName())) {
						varToAdd = sqfVariable;
					} else {
						return false;
					}
				} else {
					SQFString string = (SQFString) nodeAsPsi;
					if (SQFVariableName.nameEquals(string.getNonQuoteText(), variable.getVarName())) {
						strToAdd = string;
					} else {
						return false;
					}
				}

				boolean ignoreVariable = false;
				if (variable.isLocal() && statementDeclaredPrivateVars != null) {

					//no need to do the following code if the variable is global
					for (SQFPrivateVar statementDeclaredPrivateVar : statementDeclaredPrivateVars) {
						if (statementDeclaredPrivateVar.getVariableNameObj().equals(variableNameObj)) {
							if (statementDeclaredPrivateVar.getMaxScope() != finalVariableMaxScope) {
								//if the variable is made private in any descendant scopes, we don't want to reference those
								//because the max scopes don't match
								ignoreVariable = true;
								break;
							}
						}
					}
				}

				if (!ignoreVariable) {
					if (varToAdd != null) {
						varTargets.add(varToAdd);
					} else {
						stringTargets.add(strToAdd);
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
