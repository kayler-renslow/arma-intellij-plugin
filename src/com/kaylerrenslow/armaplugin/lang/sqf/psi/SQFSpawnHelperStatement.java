package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 09/18/2017
 */
public class SQFSpawnHelperStatement extends SQFHelperStatement {
	@Nullable
	private final SQFBlockOrExpression argsToSpawn;
	@NotNull
	private final SQFBlockOrExpression spawnCode;

	public SQFSpawnHelperStatement(@NotNull SQFStatement statement, @Nullable SQFBlockOrExpression argsToSpawn, @NotNull SQFBlockOrExpression spawnCode) {
		super(statement);
		this.argsToSpawn = argsToSpawn;
		this.spawnCode = spawnCode;
	}

	@Nullable
	public SQFBlockOrExpression getArgsToSpawn() {
		return argsToSpawn;
	}

	@NotNull
	public SQFBlockOrExpression getSpawnCode() {
		return spawnCode;
	}
}
