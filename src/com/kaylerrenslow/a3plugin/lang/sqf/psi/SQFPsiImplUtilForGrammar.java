package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kayler
 * SQF grammar util class.
 *         Created on 03/19/2016.
 */
public class SQFPsiImplUtilForGrammar{

	/** Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 * <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 * <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>
	 * @param variable Variable to test
	 * @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(SQFVariable variable){
		return SQFPsiUtil.followsSQFFunctionNameRules(variable.getVarName());
	}

	/**Get's the assigning variable for the given SQFAssignment. Example:<br>
	 * <p>
	 *     variable = 1+1; //variable is assigning variable
	 * </p>
	 */
	public static SQFVariable getAssigningVariable(SQFAssignment assignment){
		return assignment.getVariable();
	}

	/** Gets the current scope of the variable's declaration (the scope where the variable is declared private in).
	 * @param var element to get scope of
	 * @return scope
	 */
	public static SQFScope getDeclarationScope(SQFVariable var){
		SQFScope scope = SQFPsiUtil.getContainingScope(var);
		if(var.getVariableType() == SQFTypes.LANG_VAR){
			if(var.getVarName().equals("_this")){
				return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
			}
			return scope;
		}
		if(var.getVariableType() == SQFTypes.GLOBAL_VAR){
			return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
		}

		List<SQFPrivateDeclVar> varsForScope = scope.getPrivateDeclaredVars();

		while(scope != null){
			if(scope instanceof SQFFileScope){
				break;
			}
			for (SQFPrivateDeclVar varInScope : varsForScope){
				if (varInScope.getVarName().equals(var.getVarName())){
					return scope;
				}
			}
			scope = SQFPsiUtil.getContainingScope(scope.getParent());
			varsForScope = scope.getPrivateDeclaredVars();
		}
		return scope;
	}

	/** Get all the declared private variables for the given scope. This will not go deeper than the current scope.
	 * @param scope SQFScope
	 * @return list of all private variables for the given scope
	 */
	public static List<SQFPrivateDeclVar> getPrivateDeclaredVars(SQFScope scope){
		ASTNode[] statements = scope.getNode().getChildren(TokenSet.create(SQFTypes.STATEMENT));
		List<SQFPrivateDeclVar> ret = new ArrayList<>();
		SQFStatement statement;
		for(int i = 0; i < statements.length; i++){
			statement = (SQFStatement) statements[i].getPsi();
			if(statement.getPrivateDecl() != null){
				ret.addAll(statement.getPrivateDecl().getPrivateDeclVars());
			}
		}
		return ret;
	}

	/**
	 * DO NOT USE SQFPrivateDecl.getPrivateDeclVarList()<br>
	 * USE THIS INSTEAD.
	 */
	@NotNull
	public static List<SQFPrivateDeclVar> getPrivateDeclVars(SQFPrivateDecl decl){
		//although a similar method is automatically generated from the parser, it won't return a valid list when grammar is like: private "_var";
		//The valid list is only generated when grammar is like: private["_var1"];
		//This method is a workaround.
		PsiElement[] children = decl.getChildren();
		List<SQFPrivateDeclVar> list = new ArrayList<>(children.length);
		for(PsiElement child : children){
			if(child instanceof SQFPrivateDeclVar){
				list.add((SQFPrivateDeclVar) child);
			}
		}
		return list;
	}

	public static String toString (SQFPrivateDeclVar var){
		return "SQFPrivateDeclVar{"+var.getVarName()+"}";
	}

}
