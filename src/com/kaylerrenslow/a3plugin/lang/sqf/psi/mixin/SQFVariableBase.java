package com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin;

import com.kaylerrenslow.a3plugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 03/23/2016
 */
public interface SQFVariableBase {

	/**
	 * Get the variable name
	 */
	@NotNull
	SQFVariableName getVarName();

}
