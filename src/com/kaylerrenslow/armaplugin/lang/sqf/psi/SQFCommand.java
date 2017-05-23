package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommand {
	private final String commandName;

	public SQFCommand(@NotNull String commandName) {
		this.commandName = commandName;
	}

	@NotNull
	public String getCommandName() {
		return commandName;
	}
}
