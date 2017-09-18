package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.presentation.SQFFunctionItemPresentation;
import com.kaylerrenslow.armaplugin.lang.presentation.SQFVariableItemPresentation;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFVariable extends ASTWrapperPsiElement implements PsiNamedElement {
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
		List<SQFVariableReference> currentFileRefs = SQFScope.getVariableReferencesFor(this);
		PsiReference[] refsFromProviders = ReferenceProvidersRegistry.getReferencesFromProviders(this);
		PsiReference[] refsAsArray = new PsiReference[currentFileRefs.size() + refsFromProviders.length];
		int i = 0;
		for (; i < currentFileRefs.size(); i++) {
			refsAsArray[i] = currentFileRefs.get(i);
		}
		for (int j = 0; j < refsFromProviders.length; i++, j++) {
			refsAsArray[i] = refsFromProviders[j];
		}
		return refsAsArray;
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
	 * @return true if the variable starts with _, false otherwise
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
		//todo fix this method
		return var.getValue();
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
}
