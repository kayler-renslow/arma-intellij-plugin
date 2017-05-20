package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.COMMAND_TOKEN;

/**
 * Custom rules for SQF parser
 * @author Kayler
 * @since 03/04/2017
 */
public class SQFParserUtil extends GeneratedParserUtilBase {

	public static boolean external_rule_private_command(PsiBuilder b, int l, int ignore) {
		return command_rule(b, l, "private", SQFTypes.PRIVATE_COMMAND);
	}

	public static boolean external_rule_case_command(PsiBuilder b, int l, int ignore) {
		return command_rule(b, l, "case", SQFTypes.CASE_COMMAND);
	}

	private static boolean command_rule(PsiBuilder b, int l, @NotNull String commandName, IElementType exitType) {
		if (!recursion_guard_(b, l, "command")) return false;

		if (!nextTokenIs(b, COMMAND_TOKEN)) {
			return false;
		}
		if (b.getTokenText() == null || !b.getTokenText().equalsIgnoreCase(commandName)) {
			return false;
		}
		boolean r;
		PsiBuilder.Marker m = enter_section_(b);
		r = consumeToken(b, COMMAND_TOKEN);
		exit_section_(b, m, exitType, r);
		return r;
	}

}