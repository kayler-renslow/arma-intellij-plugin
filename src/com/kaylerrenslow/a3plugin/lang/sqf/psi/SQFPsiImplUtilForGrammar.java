package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFVariableBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 @author Kayler
 SQF grammar util class.
 Created on 03/19/2016. */
public class SQFPsiImplUtilForGrammar {

	/** Get the argument preceding the command */
	@Nullable
	public static PsiElement getPrefixArgument(SQFCommandExpression commandExpression) {
		ASTNode cur = commandExpression.getNode().getFirstChildNode();
		while (cur.getElementType() != SQFTypes.COMMAND) {
			if (cur.getElementType() == SQFTypes.PAREN_EXPRESSION || cur.getElementType() == SQFTypes.LITERAL_EXPRESSION || cur.getElementType() == SQFTypes.CODE_BLOCK) {
				return cur.getPsi();
			}
			cur = cur.getTreeNext();
		}
		return null;
	}

	/** Get the argument after the command. */
	@Nullable
	public static PsiElement getPostfixArgument(SQFCommandExpression commandExpression) {
		ASTNode command = commandExpression.getCommand().getNode();
		ASTNode next = PsiUtil.getNextSiblingNotWhitespace(command);
		if (next == null) {
			return null;
		}
		return next.getPsi();
	}

	public static SQFAssignment getMyAssignment(SQFVariable var) {
		if (var.getParent() instanceof SQFAssignment) {
			return (SQFAssignment) var.getParent();
		}
		return null;
	}

	public static boolean isAssigningVariable(SQFVariable var) {
		SQFAssignment assignment = var.getMyAssignment();
		return assignment != null;
	}

	public static boolean isDeclaredPrivate(SQFAssignment assignment) {
		return assignment.getNode().getFirstChildNode().getElementType() == SQFTypes.COMMAND && assignment.getNode().getFirstChildNode().getText().equals("private");
	}


	/**
	 Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>

	 @param variable Variable to test
	 @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(SQFVariable variable) {
		return SQFStatic.followsSQFFunctionNameRules(variable.getVarName());
	}

	/**
	 Get's the assigning variable for the given SQFAssignment. Example:<br>
	 <p>
	 variable = 1+1; //variable is assigning variable
	 </p>
	 */
	@NotNull
	public static SQFVariable getAssigningVariable(SQFAssignment assignment) {
		return assignment.getVariable();
	}

	//	/**
	//	 * Gets the current scope of the variable's declaration (for global variables and _this, it will be file scope). If the variable was never explicitly declared private or assigned a value, the scope will be file scope
	//	 *
	//	 * @param var element to get scope of
	//	 * @return scope
	//	 */
	//	@NotNull
	//	public static SQFScope getDeclarationScope(SQFVariable var) {
	//		if (var.isGlobalVariable()) {
	//			return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
	//		}
	//		SQFPrivatization privatization = getPrivatization(var);
	//		if (privatization != null) {
	//			return privatization.getDeclarationScope();
	//		}
	//at this point, the variable hasn't been declared private and isn't inside spawn{}
	//That leaves it to where it was first declared. We must be careful and check that the first use isn't inside a spawn and that the scope is no lower than the method parameter var's scope

		/*
		[] spawn { _var = 69; }; //spawn has its own environment

		_var = 0; //first declaration of parameter var
		if(true) then{
			_var = 1;
			if(true) then{
				_var = 3; //assume this is where this method's parameter '_var' is.
			};
		};
		*/
	//		SQFScope containingScope = SQFPsiUtil.getContainingScope(var);
	//		do {
	//			List<SQFStatement> statements = containingScope.getStatementsForScope();
	//			SQFAssignment assignment;
	//			for (SQFStatement statement : statements) {
	//				assignment = statement.getAssignment();
	//				if (assignment == null) {
	//					continue;
	//				}
	//				if (assignment.getAssigningVariable() == var) {
	//					return containingScope;
	//				}
	//				if (assignment.getAssigningVariable().varNameMatches(var)) {
	//					//name matches. check to make sure the assignment comes before where var is
	//					if (assignment.getNode().getStartOffset() < var.getNode().getStartOffset()) {
	//						return containingScope;
	//					}
	//				}
	//			}
	//			containingScope = SQFPsiUtil.getContainingScope(containingScope);
	//		} while (!(containingScope instanceof SQFFileScope));
	//
	//		return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
	//	}

	//	/**
	//	 * Gets privatization instance for the given variable. Essentially, this is getting where and how the variable is declared private.
	//	 *
	//	 * @param var the variable
	//	 * @return new instance, or returns <b>null</b> if on of the following is met: <ul><li>not declared private</li><li>global variable</li></ul>
	//	 */
	//	@Nullable
	//	public static SQFPrivatization getPrivatization(SQFVariable var) {
	//		if (var.isGlobalVariable()) {
	//			return null;
	//		}
	//		SQFScope containingScope = SQFPsiUtil.getContainingScope(var);
	//
	//		boolean _this = false; //true if var's name = _this
	//		if (var.getVariableType() == SQFTypes.LANG_VAR) {
	//			if (var.getVarName().equals("_this")) { //_this is either file scope or spawn's statement scope
	//				_this = true;
	//			} else {
	//				return new SQFPrivatization.SQFVarInheritedPrivatization(var, containingScope);
	//			}
	//		}
	//
	//		SQFScope spawnScope = SQFPsiUtil.checkIfInsideSpawn(var);
	//		boolean insideSpawn = spawnScope != null;
	//
	//		if (_this) {
	//			if (insideSpawn) {
	//				return new SQFPrivatization.SQFVarInheritedPrivatization(var, spawnScope);
	//			}
	//			return new SQFPrivatization.SQFVarInheritedPrivatization(var, SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile()));
	//		}
	//
	//
	//		/*Find where the variable is declared private*/
	//
	//		if (var.isAssigningVariable()) {
	//			if (var.getMyAssignment().isDeclaredPrivate()) { //private var = 1;
	//				return new SQFPrivatization.SQFVarInheritedPrivatization(var, containingScope);
	//			}
	//		}
	//
	//		List<SQFPrivateDeclVar> privateDeclaredVarsForScope = containingScope.getPrivateDeclaredVars();
	//		SQFScope privatizedScope = containingScope;
	//
	//		while (privatizedScope != null) {
	//			if (insideSpawn && privatizedScope == spawnScope) {
	//				return new SQFPrivatization.SQFVarInheritedPrivatization(var, spawnScope); //can't escape the spawn's environment
	//			}
	//			for (SQFPrivateDeclVar varInScope : privateDeclaredVarsForScope) {
	//				if (varInScope.getVarName().equals(var.getVarName())) {
	//					return SQFPrivatization.getPrivatization(var, varInScope.getPrivateDeclarationElement()); //declared private
	//				}
	//			}
	//			if (privatizedScope instanceof SQFFileScope) {
	//				//local variables don't need to be declared private inside control structures or code blocks
	//				//https://community.bistudio.com/wiki/Variables#Local_Variables
	//				if(!(containingScope instanceof SQFFileScope)){
	//					//we needed to wait to check this to verify that the variable wasn't declared private in an outer scope
	//					return new SQFPrivatization.SQFVarInheritedPrivatization(var, containingScope);
	//				}
	//				break;
	//			}
	//			privatizedScope = SQFPsiUtil.getContainingScope(privatizedScope.getParent());
	//			privateDeclaredVarsForScope = privatizedScope.getPrivateDeclaredVars();
	//		}
	//
	//		return null;
	//	}

	public static ArrayList<SQFStatement> getStatementsForScope(SQFScope scope) {
		ArrayList<SQFStatement> statements = new ArrayList<>();
		PsiElement[] children = scope.getChildren();
		for (PsiElement child : children) {
			if (child instanceof SQFStatement) {
				statements.add((SQFStatement) child);
			}
		}
		return statements;
	}


	public static boolean varNameMatches(SQFVariableBase variable, String otherName) {
		return variable.getVarName().equals(otherName);
	}

	public static boolean varNameMatches(SQFVariableBase variable1, SQFVariableBase variable2) {
		return variable1.varNameMatches(variable2.getVarName());
	}


	public static boolean checkIfSpawn(SQFScope scope) {
		PsiElement codeBlock = scope.getParent();
		ASTNode previous = PsiUtil.getPrevSiblingNotWhitespace(codeBlock.getNode());
		if (PsiUtil.isOfElementType(previous, SQFTypes.COMMAND)) {
			if (previous.getText().equals("spawn")) {
				return true;
			}
		}
		return false;
	}

}
