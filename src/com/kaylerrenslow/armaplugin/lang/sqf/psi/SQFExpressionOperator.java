package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFTypes.*;

/**
 * Contains either an operator XOR command
 *
 * @author kayler
 * @since 12/13/17
 */
public class SQFExpressionOperator extends ASTWrapperPsiElement {
	public SQFExpressionOperator(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * Get the {@link SQFCommand} instance if {@link #getOperatorType()} returns {@link SQFTypes#COMMAND},
	 * or it will return null.
	 *
	 * @return the {@link SQFCommand} contained in this node, or null if there isn't one.
	 */
	@Nullable
	public SQFCommand getCmd() {
		return PsiTreeUtil.findChildOfType(this, SQFCommand.class);
	}

	/**
	 * Operators that can be returned:
	 * <ul>
	 *     <li>{@link SQFTypes#COMMAND}</li>
	 *     <li>{@link SQFTypes#PLUS}</li>
	 *     <li>{@link SQFTypes#MINUS}</li>
	 *     <li>{@link SQFTypes#ASTERISK}</li>
	 *     <li>{@link SQFTypes#FSLASH}</li>
	 *     <li>{@link SQFTypes#PERC}</li>
	 *     <li>{@link SQFTypes#CARET}</li>
	 *     <li>{@link SQFTypes#AMPAMP}</li>
	 *     <li>{@link SQFTypes#BARBAR}</li>
	 *     <li>{@link SQFTypes#EXCL}</li>
	 *     <li>{@link SQFTypes#EQEQ}</li>
	 *     <li>{@link SQFTypes#NE}</li>
	 *     <li>{@link SQFTypes#GT}</li>
	 *     <li>{@link SQFTypes#GE}</li>
	 *     <li>{@link SQFTypes#LT}</li>
	 *     <li>{@link SQFTypes#LE}</li>
	 *     <li>{@link SQFTypes#GTGT}</li>
	 * </ul>
	 *
	 * @return the {@link IElementType} that contains the operator
	 */
	@NotNull
	public IElementType getOperatorType() {
		ASTNode[] nodes = getNode().getChildren(TokenSet.create(
				COMMAND, PLUS, MINUS, ASTERISK, FSLASH, PERC, CARET,
				AMPAMP, BARBAR, EXCL,
				EQEQ, NE, LT, LE, GT, GE,
				GTGT
		));
		if (nodes.length == 0) {
			throw new IllegalStateException("SQFExpressionOperator operator type should always exist");
		}
		return nodes[0].getElementType();
	}
}
