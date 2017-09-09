package com.kaylerrenslow.armaplugin.lang.header;


import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 03/31/2016
 */
public class MalformedConfigException extends GenericConfigException {

	public MalformedConfigException(@NotNull HeaderFile file, @NotNull String message) {
		super(file.getFile().getName() + " is incorrectly formatted. Message: " + message);
	}
}
