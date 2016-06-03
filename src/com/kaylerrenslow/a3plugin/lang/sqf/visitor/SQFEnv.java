package com.kaylerrenslow.a3plugin.lang.sqf.visitor;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 Created by Kayler on 06/03/2016.
 */
public class SQFEnv {
	private final SQFScope scope;
	private final List<SQFEnvVar> vars;

	public SQFEnv(SQFScope scope) {
		this(scope, new ArrayList<>());
	}

	private SQFEnv(SQFScope scope, List<SQFEnvVar> vars) {
		this.scope = scope;
		this.vars = vars;
	}

	public SQFEnv extend(SQFScope scope) {
		return new SQFEnv(scope, this.vars);
	}

	/**
	 Mark the given variable as private.

	 @param variableName name of variable
	 @param privatizer the exact element of where the variable is private (for private _var, should be _var. For private["_var"], should be "_var". For private "_var", should be "_var")
	 */
	public void privatize(String variableName, PsiElement privatizer) {
		SQFEnvVar instance = getInstance(variableName);
		instance.addPrivatizer(privatizer);
	}

	public void addUsage(SQFVariable variable) {
		SQFEnvVar match = getInstance(variable.getVarName());
		match.addUsage(variable);
	}

	public void initialize(String variableName) {
		SQFEnvVar match = getInstance(variableName);
		match.setInitialized(true);
	}

	@NotNull
	private SQFEnvVar getInstance(String variableName) {
		SQFEnvVar match = null;
		for (SQFEnvVar var : vars) {
			if (var.getVarName().equals(variableName)) {
				match = var;
				break;
			}
		}
		if (match == null) {
			match = new SQFEnvVar(variableName);
			vars.add(match);
		}
		return match;
	}

	public boolean isEmpty() {
		return vars.isEmpty();
	}

	/** Get a list of all variables inside this environment */
	public List<SQFEnvVar> getVars() {
		return vars;
	}

	public SQFScope getScope() {
		return scope;
	}

}
