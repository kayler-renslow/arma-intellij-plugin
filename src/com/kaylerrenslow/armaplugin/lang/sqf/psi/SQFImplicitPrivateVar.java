package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An implicitly made private var exist in {@link SQFImplicitPrivatizer}. A var can only be made implicitly private
 * if it is not explicitly private (use {@link SQFExplicitPrivateVar} when it is explicitly private).
 *
 * @author Kayler
 * @since 11/27/2017
 */
public class SQFImplicitPrivateVar implements SQFPrivateVar {
	@NotNull
	private final SQFVariable var;
	@NotNull
	private final SQFImplicitPrivatizer privatizer;

	public SQFImplicitPrivateVar(@NotNull SQFVariable var, @NotNull SQFImplicitPrivatizer privatizer) {
		this.var = var;
		this.privatizer = privatizer;
	}

	@NotNull
	public SQFVariable getVar() {
		return var;
	}

	@NotNull
	public SQFImplicitPrivatizer getPrivatizer() {
		return privatizer;
	}

	@NotNull
	@Override
	public List<SQFScope> getMergeScopes() {
		return privatizer.getMergeScopes();
	}

	@NotNull
	@Override
	public SQFVariableName getVariableNameObj() {
		return var.getVarNameObj();
	}

	@NotNull
	@Override
	public PsiElement getVarNameElement() {
		return var;
	}

	@NotNull
	@Override
	public SQFScope getMaxScope() {
		return privatizer.getImplicitPrivateScope();
	}
}
