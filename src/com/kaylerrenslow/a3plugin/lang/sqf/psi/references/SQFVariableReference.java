package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFRefactorableReference;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * PsiReference implementation for SQFVariable PsiElements
 * Created on 03/20/2016.
 */
public class SQFVariableReference implements SQFRefactorableReference{

	private final String variable;
	private final PsiNamedElement myElement;
	private final IElementType myVariableElementType;

	public SQFVariableReference(PsiNamedElement element, IElementType myVariableElementType) {
		variable = element.getText();
		this.myElement = element;
		this.myVariableElementType = myVariableElementType;
	}

	@Override
	public PsiElement getElement() {
		return myElement;
	}

	@Override
	public TextRange getRangeInElement() {
		return TextRange.allOf(this.getCanonicalText());
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return this.myElement;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		return myElement.getName();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		if(myVariableElementType == SQFTypes.LANG_VAR){
			throw new IncorrectOperationException("This variable can not be renamed.");
		}
		return myElement.setName(newElementName);
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		if (!(element instanceof SQFVariableNamedElement)){
			return false;
		}
		SQFVariableNamedElement other = (SQFVariableNamedElement) element;
		PsiElement selfResolve = resolve();

		boolean referenceTo = other.getName().equals(getCanonicalText()) && resolve() != other;

		if (myVariableElementType != SQFTypes.GLOBAL_VAR){
			SQFScope myScope = SQFPsiUtil.getCurrentScopeForVariable((SQFVariable) selfResolve);
			SQFScope otherScope = SQFPsiUtil.getCurrentScopeForVariable((SQFVariable) element);
			referenceTo = referenceTo && myScope == otherScope && other.getContainingFile() == selfResolve.getContainingFile();
			return referenceTo;
		}
		return referenceTo;
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public boolean isSoft() {
		return true;
	}

	@Override
	public String toString() {
		return "SQFVariableReference{" +
				"variable='" + variable + '\'' +
				'}';
	}
}
