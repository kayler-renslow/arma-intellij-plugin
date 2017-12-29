package com.kaylerrenslow.armaplugin.lang.sqf.psi.codestyle;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kayler
 * @since 12/19/2017
 */
public class SQFColorSettingsPage implements ColorSettingsPage {
	private static final Map<String, TextAttributesKey> map = new HashMap<>();

	static {
		// Things placed in map can be referenced in sample code.
		// Example: <magicVariable>_x</magicVariable> will give _x SQFSyntaxHighligher.MAGIC_VAR highlighting
		map.put("magicVariable", SQFSyntaxHighlighter.MAGIC_VAR);
		map.put("controlStructureCommand", SQFSyntaxHighlighter.CONTROL_STRUCTURE_COMMAND);
	}

	private static final AttributesDescriptor[] ATTR_DESCRIPTORS = new AttributesDescriptor[]{
			new AttributesDescriptor("Global Variable", SQFSyntaxHighlighter.GLOBAL_VAR),
			new AttributesDescriptor("Local Variable", SQFSyntaxHighlighter.LOCAL_VAR),
			new AttributesDescriptor("Magic Variable", SQFSyntaxHighlighter.MAGIC_VAR),
			new AttributesDescriptor("Command", SQFSyntaxHighlighter.COMMAND),
			new AttributesDescriptor("Comment", SQFSyntaxHighlighter.COMMENT),
			new AttributesDescriptor("String", SQFSyntaxHighlighter.STRING),
			new AttributesDescriptor("Number", SQFSyntaxHighlighter.NUM),
			new AttributesDescriptor("Operator", SQFSyntaxHighlighter.OPERATOR),
			new AttributesDescriptor("Parentheses", SQFSyntaxHighlighter.PAREN),
			new AttributesDescriptor("Braces", SQFSyntaxHighlighter.BRACE),
			new AttributesDescriptor("Brackets", SQFSyntaxHighlighter.BRACKET),
			new AttributesDescriptor("Comma", SQFSyntaxHighlighter.COMMA),
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return ArmaPluginIcons.ICON_SQF;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new SQFSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return "/*\n" +
				"    This script does absolutely nothing useful.\n" +
				"*/\n" +
				"\n" +
				"disableSerialization; //disable the serialization\n" +
				"\n" +
				"[] spawn\n" +
				"{\n" +
				"    private[\"_arr\", \"_localVar\"];\n" +
				"\n" +
				"    _localVariable = 'single quote string';\n" +
				"    meaningOfLife = 42;\n" +
				"\n" +
				"    <controlStructureCommand>if</controlStructureCommand> (1==1 and 2==2 && 42==42) then {\n" +
				"        hint \"42 is equal to 42\";\n" +
				"        _arr = [2e2, 3.1415926535, missionConfigFile];\n" +
				"    };\n" +
				"\n" +
				"    _localVar = 2 + 2;\n" +
				"\n" +
				"    {\n" +
				"        <magicVariable>_x</magicVariable> setDamage 1;\n" +
				"    } <controlStructureCommand>forEach</controlStructureCommand> units group player;\n" +
				"\n" +
				"    <controlStructureCommand>switch</controlStructureCommand> (meaningOfLife) <controlStructureCommand>do</controlStructureCommand> {\n" +
				"        <controlStructureCommand>case</controlStructureCommand> 42: { hint \"meaning of life is good\"; };\n" +
				"        <controlStructureCommand>default</controlStructureCommand> { hint \"meaning of life is wrong\"; };\n" +
				"    };\n" +
				"\n" +
				"};";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return map;
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors() {
		return ATTR_DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "SQF";
	}
}
