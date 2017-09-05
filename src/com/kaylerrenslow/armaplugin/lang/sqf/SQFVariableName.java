package com.kaylerrenslow.armaplugin.lang.sqf;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for variable names in SQF. For SQF in Arma 3, variable names aren't case sensitive.
 * Instead of passing around {@link String} and doing {@link String#equalsIgnoreCase(String)} everywhere,
 * you can just use {@link #equals(Object)} on this object with either a String as a parameter or a {@link SQFVariableName} instance.
 *
 * @author Kayler
 * @since 01/28/2017
 */
public class SQFVariableName {
	private final String name;
	private final String original;

	public SQFVariableName(@NotNull String name) {
		this.name = name.toLowerCase();
		this.original = name;
	}

	/**
	 * Check if the two given names are equal according to Arma 3 standards (will use {@link String#equalsIgnoreCase(String)} as verifier)
	 *
	 * @param name1 first name
	 * @param name2 second name
	 * @return true if the names are equal, false otherwise
	 */
	public static boolean nameEquals(@NotNull String name1, @NotNull String name2) {
		return name1.equalsIgnoreCase(name2);
	}

	public static boolean isMagicVar(@NotNull String varName) {
		String[] vars = {"_x", "_this", "this", "_exception", "_forEachIndex"};
		for (String magicVar : vars) {
			if (magicVar.equalsIgnoreCase(varName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMagicVar() {
		return isMagicVar(this.name);
	}

	/**
	 * invokes {@link #nameEquals(String, String)} with {@link #text()} and name
	 */
	public boolean nameEquals(@NotNull String name) {
		return nameEquals(this.name, name);
	}

	/**
	 * Return true if {@link #text()}.startsWith(s.toLowerCase())
	 */
	public boolean startsWith(@NotNull String s) {
		return name.startsWith(s.toLowerCase());
	}

	/**
	 * Return true if {@link #text()}.contains(s.toLowerCase())
	 */
	public boolean contains(@NotNull String s) {
		return name.contains(s.toLowerCase());
	}

	/**
	 * Get the variable name in lower-case form
	 */
	@NotNull
	public String text() {
		return name;
	}

	/**
	 * get the original variable name (one passed through constructor parameter)
	 */
	@NotNull
	public String textOriginal() {
		return original;
	}

	/**
	 * Return true if one of the following is true:
	 * <ul>
	 * <li>o==this</li>
	 * <li>o instanceof String && {@link #nameEquals(String)} with o.toString() as parameter</li>
	 * <li>o instanceof SQFVariableName && this.getName().equals(other.getName())</li>
	 * </ul>
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof String) {
			return nameEquals(o.toString());
		}
		if (o instanceof SQFVariableName) {
			SQFVariableName other = (SQFVariableName) o;
			return this.name.equals(other.name);
		}
		return false;
	}

	/**
	 * @return {@link #text()}
	 */
	@NotNull
	@Override
	public String toString() {
		return text();
	}
}
