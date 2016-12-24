package com.kaylerrenslow.a3plugin.lang.header;

import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderTypes;
import com.kaylerrenslow.a3plugin.util.FileReader;

/**
 * @author Kayler
 * @since 12/28/2015
 */
public class HeaderStatic {
	public static final String NAME = "Arma.Header";
	public static final String NAME_FOR_DISPLAY = Plugin.resources.getString("lang.header.name_for_display");
	public static final String DESCRIPTION = Plugin.resources.getString("lang.header.description");
	public static final String FILE_EXTENSION = Plugin.resources.getString("lang.header.file_extension");
	public static final String FILE_EXTENSION_DEFAULT = Plugin.resources.getString("lang.header.file_extension_default");
	public static final String HEADER_SAMPLE_CODE_TEXT = FileReader.getText("/com/kaylerrenslow/a3plugin/lang/header/codeStyle/headerSampleCode.h");

	public static final TokenSet IDENTIFERS = TokenSet.create(HeaderTypes.IDENTIFIER);
	public static final TokenSet COMMENTS = TokenSet.create(HeaderTypes.BLOCK_COMMENT, HeaderTypes.INLINE_COMMENT);
	public static final TokenSet LITERALS = TokenSet.create(HeaderTypes.HEX_LITERAL, HeaderTypes.NUMBER_LITERAL);
}
