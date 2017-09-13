package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommandExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFCommandExpression(@NotNull ASTNode node) {
		super(node);
	}

	@NotNull
	public SQFCommand getSQFCommand() {
		SQFCommand command = PsiTreeUtil.getChildOfType(this, SQFCommand.class);
		if (command == null) {
			throw new IllegalStateException("command shouldn't be null");
		}
		return command;
	}

	/**
	 * @return the {@link SQFCommandArgument} instance that comes before {@link #getSQFCommand()},
	 * or null if doesn't exist (prefixArg? COMMAND postfixArg?)
	 */
	@Nullable
	public SQFCommandArgument getPrefixArgument() {
		SQFCommandArgument[] args = PsiTreeUtil.getChildrenOfType(this, SQFCommandArgument.class);
		if (args != null && args.length > 0 && args[0].getTextOffset() < getSQFCommand().getTextOffset()) {
			return args[0];
		}
		return null;
	}

	/**
	 * @return the {@link SQFCommandArgument} instance that comes after {@link #getSQFCommand()},\
	 * or null if doesn't exist  (prefixArg? COMMAND postfixArg?)
	 */
	@Nullable
	public SQFCommandArgument getPostfixArgument() {
		SQFCommandArgument[] args = PsiTreeUtil.getChildrenOfType(this, SQFCommandArgument.class);
		if (args != null && args.length > 0) {
			int commandTextOffset = getSQFCommand().getTextOffset();
			if (args[0].getTextOffset() > commandTextOffset) {
				return args[0];
			}
			if (args.length > 1) {
				return args[1];
			}
		}
		return null;
	}
}
