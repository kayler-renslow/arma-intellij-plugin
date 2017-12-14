package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kayler
 * @since 12/13/17
 */
public class OperatorCommandDescriptors {

	private static final Map<IElementType, CommandDescriptor> map = new HashMap<>();

	static {
		map.put(SQFTypes.PLUS, getFromFile("plus"));
		map.put(SQFTypes.MINUS, getFromFile("minus"));
		map.put(SQFTypes.ASTERISK, getFromFile("asterisk"));
		map.put(SQFTypes.PERC, getFromFile("perc"));
		map.put(SQFTypes.FSLASH, getFromFile("fslash"));
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


	@Nullable
	public static CommandDescriptor get(@NotNull IElementType type) {
		return map.get(type);
	}
}
