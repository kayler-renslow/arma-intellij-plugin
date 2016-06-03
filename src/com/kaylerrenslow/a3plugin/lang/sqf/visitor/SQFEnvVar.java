package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 Created by Kayler on 06/03/2016.
 */
public class SQFEnvVar {

	private final String varName;
	private boolean isInitialized;
	private List<SQFVariable> usages;
	private List<PsiElement> privatizers = new ArrayList<>();


	SQFEnvVar(@NotNull String varName) {
		this.varName = varName;
	}

	void setInitialized(boolean initialized) {
		isInitialized = initialized;
	}

	void addPrivatizer(PsiElement privatizer) {
		this.privatizers.add(privatizer);
	}

	void addUsage(SQFVariable variable) {
		this.usages.add(variable);
	}


	@NotNull
	public String getVarName() {
		return varName;
	}

	/** Return true if the privatizer exists , or false if not declared private */
	public boolean isPrivate() {
		return privatizers.size() > 0;
	}

	/** Get the elements that made the variable private. If null, was never declared private and isPrivate() will return false. There can be more than once case where the element was declared private. */
	@Nullable
	public List<PsiElement> getPrivatizers() {
		return privatizers;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	/** Get the first usage of the variable. If null, means the variable was never actually inside the code and was just private declared somewhere */
	@Nullable
	public SQFVariable getFirstUsage() {
		return usages.get(0);
	}

	/** Get all usages of the variable in the file. If the first usage is null, was referenced in a string or something else */
	public List<SQFVariable> getUsages() {
		return usages;
	}
}
