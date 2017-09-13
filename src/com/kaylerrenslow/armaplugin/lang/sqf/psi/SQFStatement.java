package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public interface SQFStatement extends PsiElement {

	/**
	 * Gets all private vars declared private in this statement.
	 * This method will return null if the statement is incapable of making variables private. This method will return an empty list
	 * if the statement can create private variables but none were made private.
	 *
	 * @return list of private vars, or null if can't create private vars
	 */
	@Nullable
	default List<SQFPrivateVar> getDeclaredPrivateVars() {
		if (this instanceof SQFAssignmentStatement) {
			SQFAssignmentStatement assignment = (SQFAssignmentStatement) this;
			if (assignment.isPrivate()) {
				return Collections.singletonList(new SQFPrivateVar(assignment.getVar().getVarNameObj(), assignment.getVar()));
			}
		}
		if (this instanceof SQFCommandExpression) {
			SQFCommandExpression expr = (SQFCommandExpression) this;
			if (expr.getSQFCommand().getCommandName().equalsIgnoreCase("private")) {
				List<SQFPrivateVar> vars = new ArrayList<>();
				//todo
				return vars;
			}
		}
		//todo we need to check inside code blocks and control structures (https://community.bistudio.com/wiki/Variables#Scope)
		return null;
	}
}
