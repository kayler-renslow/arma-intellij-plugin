package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFArrayVal;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommandExpression;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFLiteralExpression;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class that stores a command expression that is either private["_var"] or private "_var". The private expression makes variables private.
 *
 * @author Kayler
 * @since 06/04/2016
 */
public class SQFPrivateDecl implements SQFPrivatizer {
	private final SQFCommandExpression privateDeclExpression;
	private final List<SQFPrivateDeclVar> privateDeclVars = new ArrayList<>();

	/**
	 * @param privateDeclExpression the expression
	 */
	private SQFPrivateDecl(@NotNull SQFCommandExpression privateDeclExpression) {
		this.privateDeclExpression = privateDeclExpression;
	}

	/**
	 * Get the command expression for this private declaration (usually private[] or params[]).
	 */
	public SQFCommandExpression getPrivateDeclExpression() {
		return privateDeclExpression;
	}

	@Override
	public PsiElement getPrivatizerElement() {
		return privateDeclExpression;
	}

	/**
	 * Get all variables that are declared private
	 */
	@Override
	public List<SQFPrivateDeclVar> getPrivateVars() {
		return privateDeclVars;
	}

	/**
	 * Get the a private declaration instance from the given command expression. Will return null if the expression couldn't be turned into a private decl
	 */
	@Nullable
	public static SQFPrivateDecl parse(SQFCommandExpression expression) {
		PsiElement postfix = expression.getPostfixArgument();
		String commandName = expression.getCommandName();
		SQFPrivateDecl privateDecl = null;
		if (commandName.equals("private")) { //is private []; or private ""
			if (postfix instanceof SQFLiteralExpression) {
				SQFLiteralExpression literal = (SQFLiteralExpression) postfix;
				if (literal.getArrayVal() != null) { //is private ["_var"]
					privateDecl = new SQFPrivateDecl(expression);
					SQFArrayVal array = literal.getArrayVal();
					List<SQFLiteralExpression> arrayLiterals = SQFPsiUtil.getExpressionsOfType(array, SQFLiteralExpression.class);
					for (SQFLiteralExpression literalExpression : arrayLiterals) { //iterate over each string in private ["_var1","_var2"]
						if (literalExpression.getString() != null) {
							privateDecl.privateDeclVars.add(new SQFPrivateDeclVar(literalExpression.getString(), privateDecl));
						}
					}
					return privateDecl;
				} else if (literal.getString() != null) { //is private "_var"
					privateDecl = new SQFPrivateDecl(expression);
					privateDecl.privateDeclVars.add(new SQFPrivateDeclVar(literal.getString(), new SQFPrivateDecl(expression)));
				}
			}
		}
		return privateDecl;
	}
}