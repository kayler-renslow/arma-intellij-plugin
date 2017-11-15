package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016
 */
public class Param extends BasicValueHolder {

	private final String name;

	/**
	 * @param name        the parameter name (as a variable)
	 * @param type        the type of the parameter
	 * @param description description for the parameter
	 * @param optional    true if the parameter is not required, false if it is required
	 */
	public Param(@NotNull String name, @NotNull ValueType type, @NotNull String description, boolean optional) {
		this(name, type, description, optional, Collections.emptyList());
	}

	/**
	 * @param name        the parameter name (as a variable)
	 * @param type        the type of the parameter
	 * @param description description for the parameter
	 * @param optional    true if the parameter is not required, false if it is required
	 * @param literals    a list of all literals, or an empty list if there are not literals
	 */
	public Param(@NotNull String name, @NotNull ValueType type, @NotNull String description, boolean optional, @NotNull List<String> literals) {
		super(type, description, optional, literals);
		this.name = name;
	}

	/**
	 * @return the param's name
	 */
	@NotNull
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Param{" +
				"name=" + name +
				"super.toString{" + super.toString() +
				"}}";
	}
}
