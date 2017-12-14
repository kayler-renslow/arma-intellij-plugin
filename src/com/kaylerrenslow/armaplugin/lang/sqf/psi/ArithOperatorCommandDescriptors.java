package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for getting {@link CommandDescriptor} by using {@link IElementType} rather than command names.
 * This class is not to be used for {@link SQFCommand} instances. This class is for arithmetic, boolean, or config retrieval
 * (>>) expressions.
 *
 * @author kayler
 * @since 12/13/17
 */
public class ArithOperatorCommandDescriptors {

	private static final Map<IElementType, CommandDescriptor> map = new HashMap<>();

	static {
		map.put(SQFTypes.PLUS, getFromFile("plus"));
		map.put(SQFTypes.MINUS, getFromFile("minus"));
		map.put(SQFTypes.ASTERISK, getFromFile("asterisk"));
		map.put(SQFTypes.FSLASH, getFromFile("fslash"));
		map.put(SQFTypes.PERC, getFromFile("perc"));
		map.put(SQFTypes.CARET, getFromFile("caret"));

		map.put(SQFTypes.AMPAMP, getFromFile("ampamp"));
		map.put(SQFTypes.BARBAR, getFromFile("barbar"));
		map.put(SQFTypes.EXCL, getFromFile("excl"));

		map.put(SQFTypes.EQEQ, getFromFile("eqeq"));
		map.put(SQFTypes.NE, getFromFile("ne"));
		map.put(SQFTypes.GT, getFromFile("gt"));
		map.put(SQFTypes.GE, getFromFile("ge"));
		map.put(SQFTypes.LE, getFromFile("le"));
		map.put(SQFTypes.LT, getFromFile("lt"));

		map.put(SQFTypes.GTGT, getFromFile("gtgt"));
	}

	private static CommandDescriptor getFromFile(@NotNull String operatorName) {
		return CommandDescriptor.getDescriptorFromFile("operators/" + operatorName);
	}

	/**
	 * Gets a {@link CommandDescriptor} instance for the provided type.
	 * <p>
	 * Available operators:
	 * <ul>
	 * <li>{@link SQFTypes#PLUS}</li>
	 * <li>{@link SQFTypes#MINUS}</li>
	 * <li>{@link SQFTypes#ASTERISK}</li>
	 * <li>{@link SQFTypes#FSLASH}</li>
	 * <li>{@link SQFTypes#PERC}</li>
	 * <li>{@link SQFTypes#CARET}</li>
	 * <li>{@link SQFTypes#AMPAMP}</li>
	 * <li>{@link SQFTypes#BARBAR}</li>
	 * <li>{@link SQFTypes#EXCL}</li>
	 * <li>{@link SQFTypes#EQEQ}</li>
	 * <li>{@link SQFTypes#NE}</li>
	 * <li>{@link SQFTypes#GT}</li>
	 * <li>{@link SQFTypes#GE}</li>
	 * <li>{@link SQFTypes#LT}</li>
	 * <li>{@link SQFTypes#LE}</li>
	 * <li>{@link SQFTypes#GTGT}</li>
	 * </ul>
	 *
	 * @param type the operator (non command)
	 * @return a cached {@link CommandDescriptor} for the type, or null if one doesn't exist for such type
	 * @throws IllegalArgumentException if type is equal to {@link SQFTypes#COMMAND}
	 */
	@Nullable
	public static CommandDescriptor get(@NotNull IElementType type) {
		if (type == SQFTypes.COMMAND) {
			throw new IllegalArgumentException("Can't get operator descriptor for " + type + ". This method is for non-commands only.");
		}
		return map.get(type);
	}
}
