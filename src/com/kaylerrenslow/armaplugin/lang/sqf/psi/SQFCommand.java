package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.presentation.SQFCommandItemPresentation;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFCommandReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommand extends ASTWrapperPsiElement implements PsiNamedElement {
	public SQFCommand(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public String getCommandName() {
		return getNode().getText();
	}

	@Override
	public ItemPresentation getPresentation() {
		return new SQFCommandItemPresentation(this);
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		return null;
	}

	@Override
	public String getName() {
		return getCommandName();
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		List<SQFCommand> commands = new ArrayList<>();
		String myCommandName = getCommandName();
		PsiUtil.traverseBreadthFirstSearch(getContainingFile().getNode(), astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (nodeAsPsi instanceof SQFCommand) {
				SQFCommand command = (SQFCommand) nodeAsPsi;
				if (command.getCommandName().equalsIgnoreCase(myCommandName)) {
					commands.add((SQFCommand) nodeAsPsi);
				}
			}
			return false;
		});
		if (commands.isEmpty()) {
			return PsiReference.EMPTY_ARRAY;
		}

		return new PsiReference[]{new SQFCommandReference(this, commands)};
	}

	@Override
	public PsiReference getReference() {
		PsiReference[] refs = getReferences();
		if (refs.length == 0) {
			return null;
		}
		return refs[0];
	}

}
