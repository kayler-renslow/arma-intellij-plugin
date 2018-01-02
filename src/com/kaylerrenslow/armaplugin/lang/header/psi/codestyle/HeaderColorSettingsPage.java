package com.kaylerrenslow.armaplugin.lang.header.psi.codestyle;

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
 * @since 01/02/2018
 */
public class HeaderColorSettingsPage implements ColorSettingsPage {
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
			new AttributesDescriptor("Identifier", HeaderSyntaxHighlighter.IDENTIFIER),
			new AttributesDescriptor("Keyword", HeaderSyntaxHighlighter.KEYWORD),
			new AttributesDescriptor("Preprocessor Keyword", HeaderSyntaxHighlighter.PREPROCESSOR),
			new AttributesDescriptor("String", HeaderSyntaxHighlighter.STRING),
			new AttributesDescriptor("Number", HeaderSyntaxHighlighter.NUM),
			new AttributesDescriptor("Comment", HeaderSyntaxHighlighter.COMMENT),
			new AttributesDescriptor("Operator", HeaderSyntaxHighlighter.OPERATOR),
			new AttributesDescriptor("Brace", HeaderSyntaxHighlighter.BRACE),
			new AttributesDescriptor("Bracket", HeaderSyntaxHighlighter.BRACKET),
			new AttributesDescriptor("Parenthesis", HeaderSyntaxHighlighter.PAREN),
			new AttributesDescriptor("Comma", HeaderSyntaxHighlighter.COMMA),
			//new AttributesDescriptor("StringTable Value", HeaderSyntaxHighlighter.STRINGTABLE_VALUE)
	};

	private static final Map<String, TextAttributesKey> map = new HashMap<>();

	static {
		map.put("pp", HeaderSyntaxHighlighter.PREPROCESSOR);
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return ArmaPluginIcons.ICON_HEADER;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new HeaderSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return "<pp>#include</pp> \"file.h\"\n" +
				"<pp>#include</pp> <anotherFile.h>\n" +
				"\n" +
				"/*Block comment*/\n" +
				"class TestClass {\n" +
				"\tindentifier = anotherIdentifer;\n" +
				"\tarray[] = {}; //inline comment\n" +
				"\tstring = \"hallo from the other siiide\";\n" +
				"\tmath = 1 + 1 + (42 - 2);\n" +
				"\n" +
				"\tclass NestedClass : extendClass {\n" +
				"\t\t/*Another block comment*/\n" +
				"\t\tarray[] = {var, 1, 1e1, \"\", 42, 43};\n" +
				"\t\tmath = 1 + 69 * 1;\n" +
				"\t};\n" +
				"\n" +
				"\tthingy = 0;\n" +
				"\tclass NoBodyClass;\n" +
				"};\n" +
				"\n";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return map;
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors() {
		return DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "Arma Header";
	}
}
