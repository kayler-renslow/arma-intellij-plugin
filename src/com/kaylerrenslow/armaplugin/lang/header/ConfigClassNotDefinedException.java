package com.kaylerrenslow.armaplugin.lang.header;

import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderAssignment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * This exception is created when trying to find a class inside a header config file and it doesn't exist
 *
 * @author Kayler
 * @since 03/30/2016
 */
public class ConfigClassNotDefinedException extends GenericConfigException {
	public ConfigClassNotDefinedException(@NotNull String undefinedClassName) {
		super(String.format(HeaderStatic.getHeaderBundle().getString("class-not-defined-f"), undefinedClassName));
	}

	public ConfigClassNotDefinedException(@NotNull String className, @NotNull HeaderAssignment[] assignments) {
		super(String.format(HeaderStatic.getHeaderBundle().getString("class-not-defined-with-assignments-f"), className, Arrays.toString(assignments)));
	}


}
