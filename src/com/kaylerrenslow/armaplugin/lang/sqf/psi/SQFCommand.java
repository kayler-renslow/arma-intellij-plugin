package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.presentation.SQFCommandItemPresentation;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFCommandReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommand extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
	public SQFCommand(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public String getCommandName() {
		return getText();
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
		SQFFile sqfFile = (SQFFile) getContainingFile();
		if (sqfFile == null) {
			return PsiReference.EMPTY_ARRAY;
		}
		List<SQFCommand> cmds = new ArrayList<>();
		PsiUtil.traverseBreadthFirstSearch(sqfFile.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof SQFCommand) {
				SQFCommand command = (SQFCommand) nodeAsElement;
				if (command.commandNameEquals(getCommandName())) {
					cmds.add(command);
				}
			}
			return false;
		});
		if (cmds.isEmpty()) {
			return PsiReference.EMPTY_ARRAY;
		}
		return new PsiReference[]{new SQFCommandReference(this, cmds)};
	}

	@Override
	public PsiReference getReference() {
		PsiReference[] refs = getReferences();
		if (refs.length == 0) {
			return null;
		}
		return refs[0];
	}

	/**
	 * Check if the given name is equal to the command's name ({@link #getCommandName()}), ignoring the case of the names.
	 *
	 * @param name name to check if equal to this command's name
	 * @return true if equal, false otherwise
	 * @see SQFVariableName#nameEquals(String, String)
	 */
	public boolean commandNameEquals(@NotNull String name) {
		return SQFVariableName.nameEquals(getCommandName(), name);
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this;
	}
}
