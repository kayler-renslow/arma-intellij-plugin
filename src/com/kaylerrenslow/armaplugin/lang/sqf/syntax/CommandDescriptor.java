package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016.
 */
public class CommandDescriptor {
	private final List<CommandSyntax> syntaxList;
	private final String commandName;
	private String gameVersion;
	private final BIGame game;
	private boolean deprecated = false;

	private boolean uncertain = false;

	public CommandDescriptor(@NotNull String commandName) {
		this.commandName = commandName;
		syntaxList = Collections.emptyList();
		game = BIGame.UNKNOWN;
	}

	public CommandDescriptor(@NotNull String commandName,
							 @NotNull List<CommandSyntax> syntaxList,
							 @NotNull String gameVersion,
							 @NotNull BIGame game) {
		this.syntaxList = syntaxList;
		this.commandName = commandName;
		this.gameVersion = gameVersion;
		this.game = game;
	}

	/**
	 * @return a list of {@link CommandSyntax} instances for this command
	 */
	@NotNull
	public List<CommandSyntax> getSyntaxList() {
		return syntaxList;
	}

	/**
	 * @return the command's case-sensitive name
	 */
	@NotNull
	public String getCommandName() {
		return commandName;
	}

	/**
	 * @return {@link BIGame} that describes that this command was introduced in
	 */
	@NotNull
	public BIGame getGameIntroducedIn() {
		return game;
	}

	/**
	 * @return game version of {@link #getGameIntroducedIn()}
	 */
	@NotNull
	public String getGameVersion() {
		return gameVersion;
	}

	/**
	 * @return true if the command is deprecated, false if it isn't
	 */
	public boolean isDeprecated() {
		return deprecated;
	}

	void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * @return true if the syntaxes for the command aren't exactly know and the current syntaxes are estimates
	 */
	public boolean isUncertain() {
		return uncertain;
	}

	void setUncertain(boolean uncertain) {
		this.uncertain = uncertain;
	}

}
