package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public abstract class SQFStatement extends ASTWrapperPsiElement implements SQFSyntaxNode {

	public SQFStatement(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * @return the {@link SQFIfHelperStatement} instance if the statement contains an if statement,
	 * or null if the statement isn't a valid if statement
	 */
	@Nullable
	public SQFIfHelperStatement getIfStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}

		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("if")) {
			return null;
		}
		SQFExpression condition = null;
		SQFBlockOrExpression thenn = null;
		SQFBlockOrExpression elsee = null;

		List<SQFCommandArgument> args = cmdExpr.captureArguments("if $ then $ else $");
		if (args == null) {
			args = cmdExpr.captureArguments("if $ then|exitWith $");
		}

		if (args == null) {
			return null;
		}

		condition = args.get(0).getExpr();
		thenn = args.get(1);
		if (args.size() == 3) { //if then else
			elsee = args.get(2);
		} else { //if then|exitWith
			if (thenn.getExpr() instanceof SQFLiteralExpression) {
				SQFArray array = ((SQFLiteralExpression) thenn.getExpr()).getArr();
				if (array != null) {
					//if condition then [{/*true cond*/}, {/*false cond*/}]
					List<SQFExpression> arrExps = array.getExpressions();
					if (arrExps.size() < 1) {
						return null;
					}
					SQFExpression first = arrExps.get(0).withoutParenthesis();
					thenn = new SQFBlockOrExpression.Impl(first);
					if (arrExps.size() > 1) {
						SQFExpression second = arrExps.get(1).withoutParenthesis();
						elsee = new SQFBlockOrExpression.Impl(second);
					}
				}
			}
		}


		if (thenn == null) {
			return null;
		}
		return new SQFIfHelperStatement(this, condition, thenn, elsee);
	}

	/**
	 * This will return a {@link SQFSpawnHelperStatement} only if the statement takes the form: "args <b>spawn</b> {}".
	 * It can also take the form var=args spawn {} (notice its in an assignment this time)
	 * <p>
	 * "args" is optional, but the argument after the spawn command must exist.
	 *
	 * @return the {@link SQFSpawnHelperStatement} instance if the statement contains a "args <b>spawn</b> {}" statement,
	 * or null if the statement isn't a valid spawn statement
	 */
	@Nullable
	public SQFSpawnHelperStatement getSpawnStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("spawn")) {
			return null;
		}
		SQFCommandArgument spawnPostArg = cmdExpr.getPostfixArgument();
		if (spawnPostArg == null) {
			return null;
		}
		SQFCommandArgument spawnPreArg = cmdExpr.getPrefixArgument();
		return new SQFSpawnHelperStatement(this, spawnPreArg, spawnPostArg);
	}

	/**
	 * @return the {@link SQFSwitchHelperStatement} instance if the statement contains a switch statement,
	 * or null if the statement isn't a valid switch statement
	 */
	@Nullable
	public SQFSwitchHelperStatement getSwitchStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else if (this instanceof SQFAssignmentStatement) {
			expr = ((SQFAssignmentStatement) this).getExpr();
			if (expr == null) {
				return null;
			}
			expr = expr.withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("switch")) {
			return null;
		}
		List<SQFCommandArgument> args = cmdExpr.captureArguments("switch $ do $");
		if (args == null) {
			return null;
		}
		SQFScope switchBlockScope = null;
		List<SQFCaseStatement> caseStatements = new ArrayList<>();
		Reference<SQFCommandExpression> defaultExprRef = new Reference<>();

		{ //get case statements as well as default statement
			SQFCodeBlock block = args.get(1).getBlock();
			if (block == null) {
				return null;
			}
			switchBlockScope = block.getScope();
			if (switchBlockScope != null) {
				switchBlockScope.iterateStatements(statement -> {
					if (statement instanceof SQFCaseStatement) {
						caseStatements.add((SQFCaseStatement) statement);
					} else if (statement instanceof SQFExpressionStatement) {
						SQFExpressionStatement exprStatement = (SQFExpressionStatement) statement;
						SQFExpression exprInStatement = exprStatement.getExpr();
						if (exprInStatement instanceof SQFCommandExpression) {
							SQFCommandExpression cmdExprInSwitch = (SQFCommandExpression) exprInStatement;
							if (cmdExprInSwitch.commandNameEquals("default")) {
								defaultExprRef.setValue(cmdExprInSwitch);
							}
						}
					}
				});
			}

		}

		return new SQFSwitchHelperStatement(this, caseStatements, defaultExprRef.getValue());
	}

	/**
	 * @return the {@link SQFForEachHelperStatement} instance if the statement contains a forEach statement,
	 * or null if the statement isn't a valid forEach statement
	 */
	@Nullable
	public SQFForEachHelperStatement getForEachLoopStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("forEach")) {
			return null;
		}
		List<SQFCommandArgument> args = cmdExpr.captureArguments("$ forEach $");

		if (args == null || args.size() < 2) {
			return null;
		}

		SQFBlockOrExpression code = args.get(0);
		SQFExpression iteratedExpr = args.get(1).getExpr();

		return new SQFForEachHelperStatement(this, code, iteratedExpr);
	}

	/**
	 * @return the {@link SQFForLoopHelperStatement} instance if the statement contains a for loop statement,
	 * or null if the statement isn't a valid for loop statement
	 */
	@Nullable
	public SQFForLoopHelperStatement getForLoopStatement() {
		//todo
		return null;
	}

	/**
	 * @return the {@link SQFWhileLoopHelperStatement} instance if the statement contains a while loop statement,
	 * or null if the statement isn't a valid for while statement
	 */
	@Nullable
	public SQFWhileLoopHelperStatement getWhileLoopStatement() {
		SQFExpression expr;
		if (this instanceof SQFExpressionStatement) {
			expr = ((SQFExpressionStatement) this).getExpr().withoutParenthesis();
		} else {
			return null;
		}
		if (!(expr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdExpr = (SQFCommandExpression) expr;
		if (!cmdExpr.commandNameEquals("while")) {
			return null;
		}
		SQFCommandArgument whilePostArg = cmdExpr.getPostfixArgument();
		if (whilePostArg == null) {
			return null;
		}
		SQFExpression whilePostArgExpr = whilePostArg.getExpr();
		whilePostArgExpr = whilePostArgExpr.withoutParenthesis();
		if (!(whilePostArgExpr instanceof SQFCommandExpression)) {
			return null;
		}
		SQFCommandExpression cmdDoExpr = (SQFCommandExpression) whilePostArgExpr;
		SQFCommandArgument whileCondition = cmdDoExpr.getPrefixArgument();
		SQFCommandArgument whileBody = cmdDoExpr.getPostfixArgument();
		if (whileCondition == null || whileBody == null) {
			return null;
		}
		return new SQFWhileLoopHelperStatement(this, whileCondition, whileBody);
	}

	/**
	 * @return the {@link SQFControlStructure} instance if the statement contains a control structure,
	 * or null if the statement isn't a valid control structure
	 */
	@Nullable
	public SQFControlStructure getControlStructure() {
		SQFControlStructure cs = getIfStatement();
		if (cs != null) {
			return cs;
		}
		cs = getForLoopStatement();
		if (cs != null) {
			return cs;
		}
		cs = getForEachLoopStatement();
		if (cs != null) {
			return cs;
		}
		cs = getWhileLoopStatement();
		if (cs != null) {
			return cs;
		}
		cs = getSwitchStatement();
		return cs;
	}

	/**
	 * @return the nearest ancestor {@link SQFStatement} that contains the given element, or null if not in a {@link SQFStatement}
	 * @throws IllegalArgumentException when element is a PsiComment or when it is not in an SQFFile
	 */
	@Nullable
	public static SQFStatement getStatementForElement(@NotNull PsiElement element) {
		if (element instanceof PsiComment) {
			throw new IllegalArgumentException("element is a comment");
		}
		if (element.getContainingFile() == null || !(element.getContainingFile() instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQFFile");
		}
		while (!(element instanceof SQFStatement)) {
			element = element.getParent();
			if (element == null) {
				return null;
			}
		}
		return (SQFStatement) element;
	}

	/**
	 * @return {@link #debug_getStatementTextForElement(PsiElement)} with this
	 */
	public String debug_getStatementTextForElement() {
		return debug_getStatementTextForElement(this);
	}

	/**
	 * Used for debugging. Will return the statement the given element is contained in.
	 * If the element is a PsiComment, &lt;PsiComment&gt; will be returned. Otherwise, the element's ancestor statement
	 * text will be returned with all newlines replaced with spaces.
	 * <p>
	 * If the element has no parent or no {@link SQFStatement} parent, &lt;No Statement Parent&gt; will be returned
	 *
	 * @return the text, or &lt;PsiComment&gt; if element is a PsiComment
	 */
	@NotNull
	public static String debug_getStatementTextForElement(@NotNull PsiElement element) {
		if (element instanceof PsiComment) {
			return "<PsiComment>";
		}
		if (element.getContainingFile() == null || !(element.getContainingFile() instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQFFile");
		}
		while (!(element instanceof SQFStatement)) {
			element = element.getParent();
			if (element == null) {
				return "<No Statement Parent>";
			}
		}
		return element.getText().replaceAll("\n", " ");
	}
}
