package com.kaylerrenslow.armaplugin.lang.sqf.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * @since 11/28/2017
 */
public class SQFVariableInStringReference implements PsiPolyVariantReference {

	@NotNull
	private final ResolveResult[] resolveResults;
	@NotNull
	private final SQFString string;
	@NotNull
	private final List<SQFVariable> varTargets;

	public SQFVariableInStringReference(@NotNull SQFString string, @NotNull List<SQFVariable> varTargets) {
		this.string = string;
		this.varTargets = varTargets;
		resolveResults = PsiElementResolveResult.createResults(varTargets);
	}

	@NotNull
	public SQFVariableName getVariableNameObj() {
		return varTargets.get(0).getVarNameObj();
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean incompleteCode) {
		return resolveResults;
	}

	@Override
	public PsiElement getElement() {
		return string;
	}

	@Override
	public TextRange getRangeInElement() {
		return string.getNonQuoteRangeRelativeToElement();
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return varTargets.get(0);
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return string.getNonQuoteText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		PsiFile file = PsiUtil.createFile(string.getProject(), "\"" + newElementName + "\"", SQFFileType.INSTANCE);
		Reference<SQFString> str = new Reference<>();
		PsiUtil.traverseBreadthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (nodeAsPsi instanceof SQFString) {
				str.setValue((SQFString) nodeAsPsi);
				return true;
			}
			return false;
		});
		SQFString newStr = str.getValue();
		if (newStr == null) {
			return null;
		}
		string.replace(newStr);
		for (SQFVariable var : varTargets) {
			var.setName(newElementName);
		}
		return newStr;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		return element == string || varTargets.contains(element);
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
