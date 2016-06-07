package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.presentation.SQFCommandItemPresentation;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.references.SQFCommandReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 @author Kayler
 SQF Command (e.g. createVehicle or params)
 Createdon 06/03/2016. */
public abstract class SQFCommandElement extends ASTWrapperPsiElement implements PsiNamedElement, SQFCommand {
	public SQFCommandElement(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	/**Get the name of the command (e.g. createVehicle or params or findDisplay)*/
	public String getName() {
		return getText();
	}

	@Override
	public ItemPresentation getPresentation() {
		return new SQFCommandItemPresentation(this);
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(getContainingFile(), SQFTypes.COMMAND, null, getName());
		SQFCommandElement other;
		ArrayList<SQFCommandReference> refs = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			other = ((SQFCommandElement) nodes.get(i).getPsi());
			refs.add(new SQFCommandReference(this, other));
		}

		return refs.toArray(new PsiReference[refs.size()]);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		throw new IncorrectOperationException("Can't rename commands.");
	}
}
