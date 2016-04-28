package com.kaylerrenslow.a3plugin.lang.header.psi.mixin;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassStub;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Kayler on 04/27/2016.
 */
public abstract class HeaderClassStubMixin extends ASTWrapperPsiElement implements PsiNamedElement, HeaderClassStub {
	public HeaderClassStubMixin(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
		Project project = this.getNode().getPsi().getProject();
		String extendName = this.getExtendClassName();
		if (extendName != null) {
			name += ":" + extendName;
		}
		HeaderClassStub newStub = HeaderPsiUtil.createClassDeclaration(project, name, null).getClassStub();
		this.getNode().getTreeParent().replaceChild(this.getNode(), newStub.getNode());
		return newStub;
	}
}
