package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFScope {
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
			vars.add(var.getVariableName());
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
		//todo
		return vars;
	}

	@NotNull
	static List<SQFVariable> getVariableReferencesFor(@NotNull SQFVariable variable) {
		List<SQFVariable> vars = new ArrayList<>();
		PsiFile file = variable.getContainingFile();
		if (file == null) {
			throw new IllegalArgumentException("variable doesn't have a containing file");
		}
		SQFScope containingScope = getContainingScope(variable);
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
				throw new IllegalStateException("no FileScope for file " + element);
			}
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
			throw new IllegalStateException("no FileScope for element " + element);
		}
		return fileScope;
	}
}
