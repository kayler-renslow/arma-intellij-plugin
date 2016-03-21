package com.kaylerrenslow.a3plugin.lang.sqf.psi.helpers;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.psiUtil.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommandCall;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;

/**
 * @author Kayler
 *         Created on 03/19/2016.
 */
public class SQFPsiImplUtil{

	/**
	 * Fetch the command's name in this command call.
	 */
	public static String getCommandName(SQFCommandCall commandCall) {
		ASTNode child = commandCall.getNode().getFirstChildNode();
		if (PsiUtil.isOfElementType(child, SQFTypes.COMMAND)){
			return child.getText();
		}
		child = child.getTreeNext();

		return child.getText();
	}

}
