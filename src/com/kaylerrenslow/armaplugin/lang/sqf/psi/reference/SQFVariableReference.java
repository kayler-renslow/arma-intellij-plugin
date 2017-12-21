package com.kaylerrenslow.armaplugin.lang.sqf.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 09/13/2017
 */
public abstract class SQFVariableReference implements PsiReference {
	@NotNull
	public abstract SQFVariableName getVariableNameObj();

	public static class IdentifierReference extends SQFVariableReference implements PsiPolyVariantReference {
		@NotNull
		private final SQFVariable variable;
		@NotNull
		private final List<SQFVariable> targets;
		@NotNull
		private final ResolveResult[] resolveResults;

		public IdentifierReference(@NotNull SQFVariable variable, @NotNull List<SQFVariable> targets) {
			this.variable = variable;
			this.targets = targets;

			resolveResults = PsiElementResolveResult.createResults(targets);
		}

		@NotNull
		@Override
		public SQFVariableName getVariableNameObj() {
			return variable.getVarNameObj();
		}

		@NotNull
		@Override
		public ResolveResult[] multiResolve(boolean incompleteCode) {
			return resolveResults;
		}

		@Override
		public PsiElement getElement() {
			return variable;
		}

		@Override
		public TextRange getRangeInElement() {
			return TextRange.allOf(variable.getVarName());
		}

		@Nullable
		@Override
		public PsiElement resolve() {
			return targets.get(0);
		}

		@NotNull
		@Override
		public String getCanonicalText() {
			return variable.getVarName();
		}

		@Override
		public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
			return variable.setName(newElementName);
		}

		@Override
		public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
			return null;
		}

		@Override
		public boolean isReferenceTo(PsiElement element) {
			if (element == variable) {
				return true;
			}
			if (element instanceof SQFVariable) {
				SQFVariable other = (SQFVariable) element;
				if (other.isLocal()) {
					return targets.contains(other);
				} else {
					return SQFVariableName.nameEquals(variable.getVarName(), other.getVarName());
				}
			}
			return false;
		}

		@NotNull
		@Override
		public Object[] getVariants() {
			return new Object[0];
		}

		@Override
		public boolean isSoft() {
			return false;
		}
	}

	public static class StringReference extends SQFVariableReference implements PsiPolyVariantReference {

		@NotNull
		private final ResolveResult[] resolveResults;
		@NotNull
		private final SQFVariable variable;
		@NotNull
		private final List<SQFString> stringTargets;

		public StringReference(@NotNull SQFVariable variable, @NotNull List<SQFString> stringTargets) {
			this.variable = variable;
			this.stringTargets = stringTargets;
			resolveResults = PsiElementResolveResult.createResults(stringTargets);
		}

		@NotNull
		@Override
		public SQFVariableName getVariableNameObj() {
			return variable.getVarNameObj();
		}

		@NotNull
		@Override
		public ResolveResult[] multiResolve(boolean incompleteCode) {
			return resolveResults;
		}

		@Override
		public PsiElement getElement() {
			return variable;
		}

		@Override
		public TextRange getRangeInElement() {
			return TextRange.from(0, variable.getTextLength());
		}

		@Nullable
		@Override
		public PsiElement resolve() {
			return stringTargets.get(0);
		}

		@NotNull
		@Override
		public String getCanonicalText() {
			return variable.getVarName();
		}

		@Override
		public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
			return variable.setName(newElementName);
		}

		@Override
		public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
			return null;
		}

		@Override
		public boolean isReferenceTo(PsiElement element) {
			return element == variable || stringTargets.contains(element);
		}

		@NotNull
		@Override
		public Object[] getVariants() {
			return new Object[0];
		}

		@Override
		public boolean isSoft() {
			return false;
		}

	}
}
