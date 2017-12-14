package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
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

	@Nullable
	public SQFCommand getSQFCommand() {
		return getExprOperator().getCmd();
	}

	@NotNull
	public SQFExpressionOperator getExprOperator(){
		SQFExpressionOperator op = PsiTreeUtil.getChildOfType(this, SQFExpressionOperator.class);
		if (op == null) {
			throw new IllegalStateException("a command expression should always have an expression operator");
		}
		return op;
	}

	/**
	 * Shortcut for {@link SQFVariableName#nameEquals(String, String)} on {@link #getExprOperator()}
	 *
	 * @return true if command name equals (case insensitive), or false if it doesn't equal
	 */
	public boolean commandNameEquals(@NotNull String commandName) {
		return SQFVariableName.nameEquals(getExprOperator().getText(), commandName);
	}

	/**
	 * @return the {@link SQFCommandArgument} instance that comes before {@link #getExprOperator()},
	 * or null if doesn't exist (prefixArg? COMMAND postfixArg?)
	 */
	@Nullable
	public SQFCommandArgument getPrefixArgument() {
		SQFCommandArgument[] args = PsiTreeUtil.getChildrenOfType(this, SQFCommandArgument.class);
		if (args != null && args.length > 0 && args[0].getTextOffset() < getExprOperator().getTextOffset()) {
			return args[0];
		}
		return null;
	}

	/**
	 * @return the {@link SQFCommandArgument} instance that comes after {@link #getExprOperator()},\
	 * or null if doesn't exist  (prefixArg? COMMAND postfixArg?)
	 */
	@Nullable
	public SQFCommandArgument getPostfixArgument() {
		SQFCommandArgument[] args = PsiTreeUtil.getChildrenOfType(this, SQFCommandArgument.class);
		if (args != null && args.length > 0) {
			int commandTextOffset = getExprOperator().getTextOffset();
			if (args[0].getTextOffset() > commandTextOffset) {
				return args[0];
			}
			if (args.length > 1) {
				return args[1];
			}
		}
		return null;
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}
}
