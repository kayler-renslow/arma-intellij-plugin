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
	 * @see #getPrivateVarInstances()
	 */
	@NotNull
	default Set<SQFVariableName> getPrivateVars() {
		List<SQFPrivateVar> varInstances = getPrivateVarInstances();
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
	 * @return a set containing all private variables for the scope and places them in {@link SQFPrivateVar} instances
	 */
	@NotNull
	default List<SQFPrivateVar> getPrivateVarInstances() {
		List<SQFPrivateVar> vars = new ArrayList<>();
		for (PsiElement element : getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			SQFStatement statement = (SQFStatement) element;
			List<SQFPrivateVar> declaredPrivateVars = statement.getDeclaredPrivateVars();
			if (declaredPrivateVars == null) {
				continue;
			}
			for (SQFPrivateVar privateVar : declaredPrivateVars) {
				if (privateVar.getMaxScope() == this) {
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
		SQFScope containingScope = getContainingScope(this);
		if (containingScope instanceof SQFFileScope && this instanceof SQFFileScope) {
			//file scope is outermost scope, so if current scope is file scope, then we are at outermost scope
			return vars;
		}
		for (SQFPrivateVar parentDeclaredPrivateVar : containingScope.getPrivateVarInstances()) {
			//copy over private vars
			boolean add = true;
			for (SQFPrivateVar myPrivateVar : vars) {
				if (parentDeclaredPrivateVar.getVariableNameObj().equals(myPrivateVar.getVariableNameObj())) {
					//if declared private in this scope, we don't want the containing scope's private vars that match
					add = false;
					break;
				}
				if (parentDeclaredPrivateVar.getElement().getTextOffset() > this.getTextOffset()) {
					//only private vars coming before this scope in the parent scope should be available in this scope
					add = false;
					break;
				}
			}
			if (add) {
				vars.add(parentDeclaredPrivateVar);
			}
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
			for (SQFPrivateVar privateVar : getContainingScope(variable).getPrivateVarInstances()) {
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
		for (PsiElement element : variableMaxScope.getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			boolean ignore = false;
			SQFStatement statement = (SQFStatement) element;
			if (variable.isLocal()) {
				//no need to do the following code if the variable is global
				List<SQFPrivateVar> statementDeclaredPrivateVars = statement.getDeclaredPrivateVars();
				if (statementDeclaredPrivateVars != null) {
					for (SQFPrivateVar statementDeclaredPrivateVar : statementDeclaredPrivateVars) {
						if (statementDeclaredPrivateVar.getVariableNameObj().equals(variableNameObj)) {
							if (statementDeclaredPrivateVar.getMaxScope() != variableMaxScope) {
								//if the variable is made private in any descendant scopes, we don't want to reference those
								//because the max scopes don't match
								ignore = true;
							}
							break;
						}
					}
				}
			}
			if (ignore) {
				continue;
			}
			List<SQFVariable> varTargets = new ArrayList<>();
			List<SQFString> stringTargets = new ArrayList<>();
			PsiUtil.traverseBreadthFirstSearch(statement.getNode(), astNode -> {
				PsiElement nodeAsPsi = astNode.getPsi();
				if (nodeAsPsi instanceof SQFVariable) {
					SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
					if (SQFVariableName.nameEquals(sqfVariable.getVarName(), variable.getVarName())) {
						varTargets.add((SQFVariable) nodeAsPsi);
					}
				} else if (nodeAsPsi instanceof SQFString) {
					SQFString string = (SQFString) nodeAsPsi;
					if (SQFVariableName.nameEquals(string.getNonQuoteText(), variable.getVarName())) {
						stringTargets.add((SQFString) nodeAsPsi);
						System.out.println("SQFScope.getVariableReferencesFor string.getNonQuoteText()=" + string.getNonQuoteText() + "-");
					}
				}
				return false;
			});
			System.out.println("SQFScope.getVariableReferencesFor -----------------------");
			if (!varTargets.isEmpty()) {
				vars.add(new SQFVariableReference.IdentifierReference(variable, varTargets));
			}
			if (!stringTargets.isEmpty()) {
				vars.add(new SQFVariableReference.StringReference(variable, stringTargets));
			}

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
