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
	 * @return the {@link SQFCommand} contained in this node, or null if there isn't one.
	 */
	@Nullable
	public SQFCommand getCmd() {
		return PsiTreeUtil.findChildOfType(this, SQFCommand.class);
	}

	/**
	 * @return the {@link IElementType} that contains the operator, or null if doens't contain one. This will NOT return
	 * a command element type.
	 */
	@Nullable
	public IElementType getOperatorType() {
		ASTNode[] nodes = getNode().getChildren(TokenSet.create(
				PLUS, MINUS, ASTERISK, FSLASH, PERC, CARET,
				AMPAMP, BARBAR, EXCL,
				EQEQ, NE, LT, LE, GT, GE,
				GTGT
		));
		if (nodes.length == 0) {
			return null;
		}
		return nodes[0].getElementType();
	}
}
