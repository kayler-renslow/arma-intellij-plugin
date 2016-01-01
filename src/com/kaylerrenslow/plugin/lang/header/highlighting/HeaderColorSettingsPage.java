package com.kaylerrenslow.plugin.lang.header.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.PluginIcons;
import com.kaylerrenslow.plugin.lang.header.HeaderStatic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Created by Kayler on 11/01/2015.
 */
public class HeaderColorSettingsPage implements ColorSettingsPage{
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
			new AttributesDescriptor("Comma", HeaderSyntaxHighlighter.COMMA)
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return PluginIcons.ICON_FILE;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new HeaderSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return Plugin.HEADER_COLOR_SETTINGS_PAGE_TEXT;
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
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
		return HeaderStatic.NAME;
	}
}
