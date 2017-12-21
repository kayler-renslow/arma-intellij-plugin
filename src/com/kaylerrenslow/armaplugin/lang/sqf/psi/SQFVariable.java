package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.presentation.SQFFunctionItemPresentation;
import com.kaylerrenslow.armaplugin.lang.sqf.presentation.SQFVariableItemPresentation;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFVariable extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
	public SQFVariable(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFVariableName getVarNameObj() {
		return new SQFVariableName(getVarName());
	}

	@NotNull
	public String getVarName() {
		return getText();
	}

	@NotNull
	@Override
	public PsiReference[] getReferences() {
		SQFFile sqfFile = (SQFFile) getContainingFile();
		if (sqfFile == null) {
			return PsiReference.EMPTY_ARRAY;
		}
		List<SQFVariable> vars = new ArrayList<>();
		PsiUtil.traverseBreadthFirstSearch(sqfFile.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;
				if (var.isLocal()) {
					return false;
				}
				if (SQFVariableName.nameEquals(var.getVarName(), getVarName())) {
					vars.add(var);
				}
			}
			return false;
		});
		if (vars.isEmpty()) {
			return PsiReference.EMPTY_ARRAY;
		}
		return new PsiReference[]{new SQFVariableReference.IdentifierReference(this, vars)};
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
	 * @return true if the variable starts with _, false otherwise (note that this may include magic variables)
	 */
	public boolean isLocal() {
		return getVarName().charAt(0) == '_';
	}

	/**
	 * @return true if the variable is a magic variable ({@link SQFVariableName#isMagicVar(String)}), false otherwise
	 */
	public boolean isMagicVar() {
		return SQFVariableName.isMagicVar(getVarName());
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		PsiFile file = PsiUtil.createFile(getProject(), name, SQFFileType.INSTANCE);
		Reference<SQFVariable> var = new Reference<>();
		PsiUtil.traverseBreadthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (nodeAsPsi instanceof SQFVariable) {
				var.setValue((SQFVariable) nodeAsPsi);
				return true;
			}
			return false;
		});
		SQFVariable newVar = var.getValue();
		if (newVar == null) {
			return null;
		}
		replace(newVar);
		return newVar;
	}

	@Override
	public String getName() {
		return getVarName();
	}

	@Override
	public String toString() {
		return "SQFVariable{name=" + getVarName() + "}";
	}

	/**
	 * @return true if matches, false if it doesn't
	 * @see SQFStatic#followsSQFFunctionNameRules(String)
	 */
	public boolean followsSQFFunctionNameRules() {
		return SQFStatic.followsSQFFunctionNameRules(getVarName());
	}

	@Override
	public ItemPresentation getPresentation() {
		if (!this.isLocal()) {
			if (SQFStatic.followsSQFFunctionNameRules(this.getVarName())) {
				return new SQFFunctionItemPresentation(this.getVarName(), this.getContainingFile());
			}
		}
		return new SQFVariableItemPresentation(this);
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier() {
		return this;
	}
}
