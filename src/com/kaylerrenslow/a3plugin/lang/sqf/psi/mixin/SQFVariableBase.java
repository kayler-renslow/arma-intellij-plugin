package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

/**
 * @author Kayler
 * @since 03/23/2016
 */
public interface SQFVariableBase {

	/**
	 * Get the variable name
	 */
	String getVarName();

	boolean varNameMatches(String otherName);

	boolean varNameMatches(SQFVariableBase variable);
//	SQFPrivatization getPrivatization();
}
