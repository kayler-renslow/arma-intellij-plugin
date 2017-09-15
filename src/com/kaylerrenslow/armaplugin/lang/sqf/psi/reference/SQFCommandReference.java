package com.kaylerrenslow.armaplugin.lang.sqf.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 09/14/2017
 */
public class SQFCommandReference implements PsiPolyVariantReference {
	@NotNull
	private final SQFCommand command;
	@NotNull
	private final List<SQFCommand> targets;
	@NotNull
	private final ResolveResult[] resolveResults;

	public SQFCommandReference(@NotNull SQFCommand command, @NotNull List<SQFCommand> targets) {
		this.command = command;
		this.targets = targets;

		resolveResults = PsiElementResolveResult.createResults(targets);
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean incompleteCode) {
		return resolveResults;
	}

	@Override
	public PsiElement getElement() {
		return command;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(command.getCommandName());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return targets.get(0);
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return command.getCommandName();
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
		return element == command || targets.contains(element);
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
