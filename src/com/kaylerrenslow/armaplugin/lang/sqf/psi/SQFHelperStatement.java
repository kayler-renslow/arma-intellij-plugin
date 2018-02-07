package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * A helper class that specifies that a {@link SQFStatement} follows a certain structure to get it's
 * own class, i.e. {@link SQFSpawnHelperStatement}. *
 *
 * @author Kayler
 * @since 02/06/2018
 */
public abstract class SQFHelperStatement {
	@NotNull
	private final SQFStatement statement;

	public SQFHelperStatement(@NotNull SQFStatement statement) {
		this.statement = statement;
	}

	/**
	 * @return the {@link SQFStatement} that this helper is created around
	 */
	@NotNull
	public SQFStatement getStatement() {
		return statement;
	}
}
