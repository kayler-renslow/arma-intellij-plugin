package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for marking statements that can implicitly make variables private
 *
 * @author Kayler
 * @since 11/27/2017
 */
public interface SQFImplicitPrivatizer {
	/**
	 * @return a scope where variables can be made implicitly private
	 */
	@NotNull
	SQFScope getImplicitPrivateScope();

	/**
	 * @return a list of scopes where variables share an environment with {@link #getImplicitPrivateScope()}
	 */
	@NotNull
	List<SQFScope> getMergeScopes();
}
