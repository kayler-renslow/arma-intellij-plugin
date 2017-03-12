package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatization;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFParamsStatement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateAssignmentPrivatizer;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SQF grammar util class.
 *
 * @author Kayler
 * @since 03/19/2016
 */
public class SQFPsiImplUtilForGrammar {

	@Nullable
	public static SQFCodeBlock getCodeBlock(SQFArrayEntry arrayEntry) {
		if (arrayEntry.getExpression() instanceof SQFCodeBlock) {
			return (SQFCodeBlock) arrayEntry.getExpression();
		}
		return null;
	}

	/**
	 * Get the argument preceding the command
	 */
	@Nullable
	public static PsiElement getPrefixArgument(SQFCommandExpression commandExpression) {
		ASTNode cur = commandExpression.getNode().getFirstChildNode();
		while (cur.getElementType() != SQFTypes.COMMAND) {
			if (cur.getElementType() == SQFTypes.UNARY_EXPRESSION) {
				SQFUnaryExpression unaryExpression = (SQFUnaryExpression) cur.getPsi();
				if (unaryExpression.getExpression() == null) {
					return null;
				}
				cur = unaryExpression.getExpression().getNode(); //no need to jump outside after next if statement since this is technically the prefix command
			}
			if (cur.getElementType() != TokenType.WHITE_SPACE) {
				return cur.getPsi();
			}
			cur = cur.getTreeNext();
		}
		return null;
	}

	/**
	 * Get the argument after the command.
	 */
	@Nullable
	public static PsiElement getPostfixArgument(SQFCommandExpression commandExpression) {
		ASTNode command = commandExpression.getCommand().getNode();
		ASTNode next = PsiUtil.getNextSiblingNotWhitespace(command);
		if (next == null) {
			return null;
		}
		if (next.getPsi() instanceof SQFUnaryExpression) {
			return ((SQFUnaryExpression) next.getPsi()).getExpression();
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
		return assignment.getPrivateCommand() != null;
	}


	/**
	 * Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 * <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 * <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>
	 *
	 * @param variable Variable to test
	 * @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(SQFVariable variable) {
		return SQFStatic.followsSQFFunctionNameRules(variable.getVarName());
	}

	/**
	 * Get's the assigning variable for the given SQFAssignment. Example:<br>
	 * <p>
	 * variable = 1+1; //variable is assigning variable
	 * </p>
	 */
	@NotNull
	public static SQFVariable getAssigningVariable(SQFAssignment assignment) {
		return assignment.getVariable();
	}

	/**
	 * Gets the current scope of the variable's declaration (for global variables and _this, it will be file scope). If the variable was never explicitly declared private or assigned a value, the scope will be file scope
	 *
	 * @param var element to get scope of
	 * @return scope
	 */
	@NotNull
	public static SQFScope getDeclarationScope(SQFVariable var) {
		if (var.isGlobalVariable()) {
			return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
		}
		SQFPrivatization privatization = getPrivatization(var);
		if (privatization != null) {
			return privatization.getDeclarationScope();
		}
		//at this point, the variable hasn't been declared private and isn't inside spawn{}
		//That leaves it to where it was first declared. We must be careful and check that the first use isn't inside a spawn and that the scope is no lower than the method parameter var's scope

		/*
		[] spawn { _var = 69; }; //spawn has its own environment (this scenario should be caught from getPrivatization())

		_var = 0; //first declaration of parameter var
		if(true) then{
			_var = 1;
			if(true) then{
				_var = 3; //assume this is where this method's parameter '_var' is.
			};
		};
		*/
		SQFScope containingScope = SQFPsiUtil.getContainingScope(var);
		SQFScope returnScope = null;
		do {
			List<SQFStatement> statements = containingScope.getStatementsForScope();
			SQFAssignment assignment;
			for (SQFStatement statement : statements) {
				assignment = statement.getAssignment();
				if (assignment == null) {
					continue;
				}
				if (assignment.getAssigningVariable() == var) {
					returnScope = containingScope;
				}
				if (assignment.getAssigningVariable().getVarName().equals(var)) {
					//name matches. check to make sure the assignment comes before where var is
					if (assignment.getNode().getStartOffset() < var.getNode().getStartOffset()) {
						returnScope = containingScope; //do not return yet. We need to go up as high as possible
					}
				}
			}
			containingScope = SQFPsiUtil.getContainingScope(containingScope);
		} while (!(containingScope instanceof SQFFileScope));
		if (returnScope != null) {
			return returnScope;
		}
		return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
	}


	/**
	 * Gets privatization instance for the given variable. Essentially, this is getting where and how the variable is declared private.
	 *
	 * @param var the variable
	 * @return new instance, or returns <b>null</b> if on of the following is met: <ul><li>not declared private</li><li>global variable</li></ul>
	 */
	@Nullable
	public static SQFPrivatization getPrivatization(SQFVariable var) {
		if (var.isGlobalVariable()) {
			return null;
		}
		SQFScope containingScope = SQFPsiUtil.getContainingScope(var);

		boolean _this = false; //true if var's name = _this
		if (var.getVariableType() == SQFTypes.LANG_VAR) {
			if (var.getVarName().nameEquals("_this")) { //_this is either file scope or spawn's statement scope
				_this = true;
			} else {
				return new SQFPrivatization.SQFVarInheritedPrivatization(var, containingScope);
			}
		}

		SQFScope spawnScope = SQFPsiUtil.checkIfInsideSpawn(var);
		boolean insideSpawn = spawnScope != null;

		if (_this) {
			if (insideSpawn) {
				return new SQFPrivatization.SQFVarInheritedPrivatization(var, spawnScope);
			}
			return new SQFPrivatization.SQFVarInheritedPrivatization(var, SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile()));
		}


		/*Find where the variable is declared private*/

		SQFAssignment varAssignment = var.getMyAssignment();
		if (varAssignment != null) {
			if (varAssignment.isDeclaredPrivate()) { //private var = 1;
				return new SQFPrivatization.SQFPrivateAssignment(var, new SQFPrivateAssignmentPrivatizer(varAssignment));
			}
		}

		/*
		Check if the containing scope is a part of a for spec loop and this var's var name matches a private var in the first block. If it is, force the privatization into the first code block.
		Example: for [{private _i=0},{true},{}] do{};
		*/
		SQFCodeBlock containScopeCodeBlock = containingScope.getCodeBlock();
		if (containScopeCodeBlock != null) {
			PsiElement cursor = containScopeCodeBlock;
			SQFArrayVal array = null;
			while (!(cursor instanceof SQFStatement)) {
				cursor = cursor.getParent();
				if (cursor instanceof SQFArrayVal) {
					array = (SQFArrayVal) cursor;
				}
			}
			SQFStatement cursorStatement = (SQFStatement) cursor;
			if (cursorStatement.getExpression() != null && cursorStatement.getExpression() instanceof SQFCommandExpression) {
				SQFCommandExpression forCommandExp = (SQFCommandExpression) cursorStatement.getExpression();
				if (forCommandExp.getCommandName().equals("for")) {
					if (array == null) { //the containing scope wasn't inside the array itself and may be inside the postfix expression of do (e.g. for[{},{},{}] do{})
						if (forCommandExp.getPostfixArgument() instanceof SQFCommandExpression) {
							SQFCommandExpression postfixArgumentCommand = (SQFCommandExpression) forCommandExp.getPostfixArgument();
							if (postfixArgumentCommand.getPrefixArgument() instanceof SQFLiteralExpression) {
								SQFLiteralExpression literalExpression = (SQFLiteralExpression) postfixArgumentCommand.getPrefixArgument();
								array = literalExpression.getArrayVal();
							}
						}
					}

					if (array != null) { //definitely not the for spec loop if null
						if (array.getArrayEntryList().size() > 0) {
							SQFCodeBlock firstCodeBlock = array.getArrayEntryList().get(0).getCodeBlock();
							if (firstCodeBlock != null) {
								if (firstCodeBlock.getLocalScope() != null) { //may be formatted wrong since no type checking
									List<SQFStatement> statementList = firstCodeBlock.getLocalScope().getStatementList();
									for (SQFStatement statement : statementList) {
										if (statement.getAssignment() != null) {
											if (statement.getAssignment().getAssigningVariable().getVarName().equals(var)) {
												return SQFPrivatization.getPrivatization(var, firstCodeBlock.getLocalScope());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		List<SQFPrivateDeclVar> privateDeclaredVarsForScope = containingScope.getPrivateVars();
		SQFScope privatizedScope = containingScope;

		while (true) {
			if (insideSpawn && privatizedScope == spawnScope) { //inside spawn and not declared private elsewhere
				return new SQFPrivatization.SQFVarInheritedPrivatization(var, spawnScope); //can't escape the spawn's environment
			}
			for (SQFPrivateDeclVar varInScope : privateDeclaredVarsForScope) {
				if (varInScope.getVarName().equals(var.getVarName())) {
					return SQFPrivatization.getPrivatization(var, varInScope.getPrivatizer()); //declared private
				}
			}
			if (privatizedScope instanceof SQFFileScope) {
				//local variables don't need to be declared private inside control structures or code blocks
				//https://community.bistudio.com/wiki/Variables#Local_Variables
				if (!(containingScope instanceof SQFFileScope)) { //we needed to wait to check this to verify that the variable wasn't declared private in an outer scope
					return new SQFPrivatization.SQFVarInheritedPrivatization(var, containingScope);
				}
				break;
			}
			privatizedScope = SQFPsiUtil.getContainingScope(privatizedScope.getParent());
			privateDeclaredVarsForScope = privatizedScope.getPrivateVars();
		}

		return null;
	}


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

	/**
	 * Get all the declared private variables for the given scope. This will not go deeper than the current scope.
	 *
	 * @param scope SQFScope
	 * @return list of all private variables for the given scope
	 */
	public static List<SQFPrivateDeclVar> getPrivateVars(SQFScope scope) {
		List<SQFStatement> statements = PsiUtil.findChildrenOfType(scope, SQFStatement.class);
		Iterator<SQFStatement> statementIterator = statements.iterator();
		SQFStatement currentStatement;

		List<SQFPrivateDeclVar> ret = new ArrayList<>();

		SQFAssignment assignment;
		String commandName;
		SQFCommandExpression expression;

		while (statementIterator.hasNext()) {
			currentStatement = statementIterator.next();
			if (currentStatement.getAssignment() != null) {
				assignment = currentStatement.getAssignment();
				if (assignment.isDeclaredPrivate()) {
					ret.add(new SQFPrivateDeclVar(assignment.getAssigningVariable(), new SQFPrivateAssignmentPrivatizer(assignment)));
				}
			} else if (currentStatement.getExpression() != null && currentStatement.getExpression() instanceof SQFCommandExpression) {
				expression = (SQFCommandExpression) currentStatement.getExpression();
				commandName = expression.getCommandName();
				if (commandName.equals("private")) { //is private []; or private ""
					SQFPrivateDecl privateDecl = SQFPrivateDecl.parse(expression);
					if (privateDecl != null) {
						ret.addAll(privateDecl.getPrivateVars());
					}
				} else if (commandName.equals("params")) { //is params statement
					SQFParamsStatement paramsStatement = SQFParamsStatement.parse(expression);
					if (paramsStatement != null) {
						ret.addAll(paramsStatement.getPrivateVars());
					}
				}
			}
		}
		return ret;
	}


	public static PsiElement getPrivatizerElement(SQFScope scope) {
		return scope;
	}

	public static TextRange getNonQuoteRangeRelativeToFile(SQFString string) {
		return TextRange.from(string.getTextOffset() + 1, string.getTextLength() - 2);
	}

	public static TextRange getNonQuoteRangeRelativeToElement(SQFString string) {
		return TextRange.from(1, string.getTextLength() - 2);
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

	public static SQFCodeBlock getCodeBlock(SQFScope scope) {
		if (scope instanceof SQFFileScope) {
			return null;
		}
		return (SQFCodeBlock) scope.getParent();
	}


	public static String getCommandName(SQFCommandExpression commandExpression) {
		return commandExpression.getCommand().getText();
	}

}
