package com.kaylerrenslow.armaplugin.lang.sqf.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 09/13/2017
 */
public abstract class SQFVariableReference implements PsiPolyVariantReference {
	@NotNull
	public abstract SQFVariableName getVariableNameObj();

	class IdentifierReference extends SQFVariableReference {
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
			return null;
		}

		@Override
		public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
			return null;
		}

		@Override
		public boolean isReferenceTo(PsiElement element) {
			return element == variable || targets.contains(element);
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

	class StringReference extends SQFVariableReference {
		@NotNull
		@Override
		public SQFVariableName getVariableNameObj() {
			return null;
		}

		@NotNull
		@Override
		public ResolveResult[] multiResolve(boolean incompleteCode) {
			return new ResolveResult[0];
		}

		@Override
		public PsiElement getElement() {
			return null;
		}

		@Override
		public TextRange getRangeInElement() {
			return null;
		}

		@Nullable
		@Override
		public PsiElement resolve() {
			return null;
		}

		@NotNull
		@Override
		public String getCanonicalText() {
			return null;
		}

		@Override
		public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
			return null;
		}

		@Override
		public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
			return null;
		}

		@Override
		public boolean isReferenceTo(PsiElement element) {
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
}
