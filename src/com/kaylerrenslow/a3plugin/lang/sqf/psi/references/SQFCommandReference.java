package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFCommandElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * This type of reference is used between two commands.
 * Created on 06/03/2016.
 */
public class SQFCommandReference implements PsiReference {
	private final SQFCommandElement target, command;

	public SQFCommandReference(SQFCommandElement command, SQFCommandElement target) {
		this.command = command;
		this.target = target;
	}

	@Override
	public PsiElement getElement() {
		return command;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(command.getText());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return target;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return target.getText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		throw new IncorrectOperationException("Commands can't be renamed");
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (!(element instanceof SQFCommandElement)) {
			return false;
		}
		PsiElement selfResolve = resolve();
		if (selfResolve == element || selfResolve == null) {
			return false;
		}
		SQFCommandElement other = (SQFCommandElement) element;

		return selfResolve.getText().equals(other.getText());
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public String toString() {
		return "SQFVariableReference{" +
				"target=" + target +
				", command=" + command +
				'}';
	}

	@Override
	public boolean isSoft() {
		return false;
	}
}
