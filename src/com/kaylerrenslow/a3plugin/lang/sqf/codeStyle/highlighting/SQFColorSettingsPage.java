package com.kaylerrenslow.a3plugin.lang.sqf.codeStyle.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.kaylerrenslow.a3plugin.PluginIcons;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Created by Kayler on 11/01/2015.
 */
public class SQFColorSettingsPage implements ColorSettingsPage{
	private static final AttributesDescriptor[] ATTR_DESCRIPTORS = new AttributesDescriptor[]{
			new AttributesDescriptor("Global Variable", SQFSyntaxHighlighter.GLOBAL_VAR),
			new AttributesDescriptor("Local Variable", SQFSyntaxHighlighter.LOCAL_VAR),
			new AttributesDescriptor("Command", SQFSyntaxHighlighter.COMMAND),
			new AttributesDescriptor("Comment", SQFSyntaxHighlighter.COMMENT),
			new AttributesDescriptor("Constant", SQFSyntaxHighlighter.CONSTANT),
			new AttributesDescriptor("Keyword", SQFSyntaxHighlighter.KEYWORD),
			new AttributesDescriptor("String", SQFSyntaxHighlighter.STRING),
			new AttributesDescriptor("Number", SQFSyntaxHighlighter.NUM),
			new AttributesDescriptor("Operator", SQFSyntaxHighlighter.OPERATOR),
			new AttributesDescriptor("Parenthesis", SQFSyntaxHighlighter.PAREN),
			new AttributesDescriptor("Brace", SQFSyntaxHighlighter.BRACE),
			new AttributesDescriptor("Bracket", SQFSyntaxHighlighter.BRACKET),
			new AttributesDescriptor("Comma", SQFSyntaxHighlighter.COMMA),
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return PluginIcons.ICON_SQF;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new SQFSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return SQFStatic.SQF_SAMPLE_CODE_TEXT;
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
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
		return SQFStatic.NAME;
	}

}
