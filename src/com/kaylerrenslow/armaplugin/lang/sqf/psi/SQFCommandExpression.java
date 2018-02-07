package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public class SQFCommandExpression extends ASTWrapperPsiElement implements SQFExpression {
	public SQFCommandExpression(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * This method exists to make it easier to capture all {@link SQFCommandArgument} instances
	 * from this {@link SQFCommandExpression} and any descendant {@link SQFCommandExpression} instances
	 * as well. If a {@link SQFParenExpression} is found inside a {@link SQFCommandArgument},
	 * this method will traverse all the way down the hierarchy until a {@link SQFCommandExpression} is found
	 * or anything other than a {@link SQFParenExpression}. In the event that there is not {@link SQFCommandExpression} found,
	 * the {@link SQFCommandArgument} will be added to the list. If a {@link SQFCommandExpression} is found
	 * in the {@link SQFParenExpression}, the {@link SQFParenExpression} will be ignored.
	 * <p>
	 * In the event that a pattern can not be matched, null will be returned.
	 * <p>
	 * How this works is you pass in a pattern to match. It's basically SQF code, but only commands are matched
	 * by word and arguments are matched with $. If a $ is absent, then the pattern is saying don't look for an argument
	 * at that place.
	 * <p>
	 * There is also functionality to match one of a list of commands. To do this, type COMMAND_1|COMMAND_2 where
	 * COMMAND_1 and COMMAND_2 are you commands. For example, you can do "if $ then|exitWith $" to match
	 * then or exitWith.
	 * <p>
	 * The matching works by tokenizing all words with a space as the delimeter. As you would expect, the syntax
	 * is like SQF, so passing something like "format $ $" doesn't make sense and will result in an exception thrown
	 * (just to be clear, format is an SQF command).
	 * Also, passing something like "format format" means that a pattern can only be matched when a {@link SQFCommandExpression}
	 * has a {@link SQFCommandExpression} as it's right argument rather than a {@link SQFCommandArgument} as right argument.
	 * <p>
	 * Examples:
	 * <ul>
	 * <li>Match format command with an array afterwards: "format $"</li>
	 * <li>Match disableSerialization command with no arguments: "disableSerialization"</li>
	 * <li>Match spawn command with a left and right argument: "$ spawn $"</li>
	 * </ul>
	 *
	 * @return a list of arguments that were captured by $. The list will have items appended in the order they are matched.
	 * If returns null, then the pattern could not be matched.
	 * @throws IllegalArgumentException for when the pattern was invalid.
	 */
	@Nullable
	public List<SQFCommandArgument> captureArguments(@NotNull String pattern) {
		String[] tokens = pattern.trim().split("\\s");
		{ //validate pattern
			boolean expectCommand = false;
			int ti = 0;
			for (String token : tokens) {
				if (expectCommand) {
					if (token.equals("$")) {
						throw new IllegalArgumentException("Expected a command, got " + token + " at index " + ti);
					}
					expectCommand = false;
				} else {
					if (token.equals("$")) {
						expectCommand = true;
					}
				}
				ti += token.length() + 1;
			}
		}

		int ti = 0;
		List<SQFCommandArgument> args = new ArrayList<>();
		SQFCommandExpression cursor = this;
		while (true) {
			SQFExpressionOperator op = cursor.getExprOperator();

			SQFCommandArgument pre = cursor.getPrefixArgument();
			SQFCommandArgument post = cursor.getPostfixArgument();
			if (pre != null) {
				if (ti >= tokens.length) {
					return null;
				}
				if (!tokens[ti].equals("$")) {
					return null;
				}
				ti++;
				args.add(pre);
			}

			if (ti >= tokens.length) {
				return null;
			}
			String[] barred = tokens[ti].split("\\|");
			boolean matched = false;
			for (String t : barred) {
				if (SQFVariableName.nameEquals(t, op.getText())) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				return null;
			}
			ti++;

			if (post != null) {
				SQFExpression postExpr = post.getExpr().withoutParenthesis();
				if (postExpr instanceof SQFCommandExpression) {
					cursor = (SQFCommandExpression) postExpr;
				} else {
					if (ti >= tokens.length) {
						return null;
					}
					if (!tokens[ti].equals("$")) {
						return null;
					}
					args.add(post);
					break;
				}
			} else {
				if (ti < tokens.length) {
					//the pattern is trying to match more than is available
					return null;
				}
				break;
			}
		}
		return args;
	}

	@Nullable
	public SQFCommand getSQFCommand() {
		return getExprOperator().getCmd();
	}

	@NotNull
	public SQFExpressionOperator getExprOperator() {
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
