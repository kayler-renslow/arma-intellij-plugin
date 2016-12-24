package com.kaylerrenslow.a3plugin.lang.shared.formatting;

import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author Kayler
 * @since 03/25/2016
 */
public class CodeStyleUtil {

	public static class ClassBraceStyle {
		public static boolean endOfLine(CodeStyleSettings settings) {
			return settings.CLASS_BRACE_STYLE == CodeStyleSettings.END_OF_LINE;
		}

		public static boolean nextLineIfWrapped(CodeStyleSettings settings) {
			return settings.CLASS_BRACE_STYLE == CodeStyleSettings.NEXT_LINE_IF_WRAPPED;
		}

		public static boolean nextLine(CodeStyleSettings settings) {
			return settings.CLASS_BRACE_STYLE == CodeStyleSettings.NEXT_LINE;
		}

		public static boolean nextLineShifted(CodeStyleSettings settings) {
			return settings.CLASS_BRACE_STYLE == CodeStyleSettings.NEXT_LINE_SHIFTED;
		}

		public static boolean nextLineEachShifted(CodeStyleSettings settings) {
			return settings.CLASS_BRACE_STYLE == CodeStyleSettings.NEXT_LINE_SHIFTED2;
		}

		public static void setClassBraceStyle(CodeStyleSettings settings, int setting) {
			settings.CLASS_BRACE_STYLE = setting;
		}
	}

	public static class ArrayInitializerStyle {
		public static boolean doNotWrap(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_WRAP == CodeStyleSettings.DO_NOT_WRAP;
		}

		public static boolean wrapIfLong(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_WRAP == CodeStyleSettings.WRAP_AS_NEEDED;
		}

		public static boolean chopDownIfLong(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_WRAP == CodeStyleSettings.WRAP_AS_NEEDED;
		}

		public static boolean alwaysWrap(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_WRAP == CodeStyleSettings.WRAP_ALWAYS;
		}

		public static boolean newLineAfterLBrace(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_LBRACE_ON_NEXT_LINE;
		}

		public static boolean newLineAfterRBrace(CodeStyleSettings settings) {
			return settings.ARRAY_INITIALIZER_RBRACE_ON_NEXT_LINE;
		}

	}
}
