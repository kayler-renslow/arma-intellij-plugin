package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFForLoopBase;
import com.kaylerrenslow.a3plugin.util.TraversalObjectFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         SQF grammar util class.
 *         Created on 03/19/2016.
 */
public class SQFPsiImplUtilForGrammar {

	public static SQFScope getLoopScope(SQFForLoopBase loop){
		SQFCodeBlock  codeBlock = (SQFCodeBlock)loop.getNode().getChildren(TokenSet.create(SQFTypes.CODE_BLOCK))[0].getPsi();
		return codeBlock.getLocalScope();
	}

	@Nullable
	public static String[] getIterationVariables(SQFForLoopBase forLoop){
		if(forLoop instanceof SQFLoopFor){
			SQFLoopFor loop = (SQFLoopFor)forLoop;
			List<SQFForLoopIterVarInit> vars = loop.getForLoopIterVarInitList();
			if (vars.size() == 0) {
				return null;
			}
			String[] varNames = new String[vars.size()];
			int i = 0;
			for(SQFForLoopIterVarInit iterVarInit : vars){
				if(iterVarInit.getStatement().getAssignment() == null){
					varNames[i] = "";
					i++;
				}
				varNames[i] = iterVarInit.getStatement().getAssignment().getAssigningVariable().getVarName();
				i++;
			}
			return varNames;
		}
		if(forLoop instanceof SQFLoopForFrom){
			SQFLoopForFrom loop = (SQFLoopForFrom)forLoop;
			return new String[]{loop.getString().getNonQuoteText()};
		}
		if(forLoop instanceof SQFLoopForEach){
			return new String[]{"_x"};
		}

		return null;
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
	 * Gets the current scope of the variable's declaration (for global variables and _this, it will be file scope)
	 *
	 * @param var element to get scope of
	 * @return scope
	 */
	public static SQFScope getDeclarationScope(SQFVariable var) {
		SQFScope containingScope = SQFPsiUtil.getContainingScope(var);

		if (containingScope instanceof SQFFileScope) {
			return containingScope; //no need to do anything special
		}

		boolean _this = false; //true if var's name = _this
		if (var.getVariableType() == SQFTypes.LANG_VAR) {
			if (var.getVarName().equals("_this")) { //_this is either file scope or spawn's statement scope
				_this = true;
			} else {
				return containingScope;
			}
		}
		if (var.getVariableType() == SQFTypes.GLOBAL_VAR) {
			return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
		}

		SQFScope spawnScope = SQFPsiUtil.checkIfInsideSpawn(var);
		boolean insideSpawn = spawnScope != null;

		if (_this) {
			if (insideSpawn) {
				return spawnScope;
			}
			return SQFPsiUtil.getFileScope((SQFFile) var.getContainingFile());
		}


		//find where the variable is declared private
		List<SQFPrivateDeclVar> privateDeclaredVarsForScope = containingScope.getPrivateDeclaredVars();
		SQFScope privatizedScope = containingScope;

		while (privatizedScope != null) {
			if (insideSpawn && privatizedScope == spawnScope) {
				return spawnScope; //can't escape the spawn's environment
			}
			for (SQFPrivateDeclVar varInScope : privateDeclaredVarsForScope) {
				if (varInScope.getVarName().equals(var.getVarName())) {
					return privatizedScope; //declared private
				}
			}
			if (privatizedScope instanceof SQFFileScope) {
				break;
			}
			privatizedScope = SQFPsiUtil.getContainingScope(privatizedScope.getParent());
			privateDeclaredVarsForScope = privatizedScope.getPrivateDeclaredVars();
		}

		//at this point, the variable hasn't been declared private and isn't inside spawn{}
		//That leaves it to where it was first declared. We must be careful and check that the first use isn't inside a spawn and that the scope is no lower than the method parameter var's scope

		/*
		[] spawn { var = 69; }; //spawn has its own environment

		var = 0; //first declaration of parameter var
		if(true) then{
			var = 1;
			if(true) then{
				var = 3; //assume this is where this method's parameter 'var' is.
			};
		};

		*/


		//use breadth first search to find the first use
		GetLocalVarDeclarationTraversal traversal = new GetLocalVarDeclarationTraversal(var);
		PsiUtil.traverseBreadthFirstSearch(var.getContainingFile().getNode(), traversal);
		SQFVariable firstDeclaration = traversal.getFirstDeclaration();
		if (firstDeclaration == var) {
			return containingScope;
		}
		if (firstDeclaration == null) {
			return containingScope;
		}

		return SQFPsiUtil.getContainingScope(firstDeclaration);
	}

	private static class GetLocalVarDeclarationTraversal implements TraversalObjectFinder<ASTNode> {
		private final SQFVariable var;
		private boolean stopped = false;
		private boolean traverseFoundChildren = true;
		private SQFVariable firstDeclaration;

		GetLocalVarDeclarationTraversal(SQFVariable var) {
			this.var = var;
		}

		@Override
		public void found(@NotNull ASTNode astNode) {
			//check to make sure that the found variable comes BEFORE this.var
			if(astNode.getStartOffset() > this.var.getNode().getStartOffset()){
				this.traverseFoundChildren = false; //no need to go deeper on this node since it comes after this.var
				return;
			}
			if (astNode.getElementType() == SQFTypes.CODE_BLOCK) {
				ASTNode previous = PsiUtil.getPrevSiblingNotWhitespace(astNode);
				if (PsiUtil.isOfElementType(previous, SQFTypes.COMMAND)) {
					if (previous.getText().equals("spawn")) {
						this.traverseFoundChildren = false; //don't traverse spawn statements
					}
				}
			}
			if (astNode.getElementType() == SQFTypes.LOCAL_VAR) {
				if (astNode.getText().equals(var.getVarName())) {
					//now check if found variable's scope is a container for this.var's scope
					ArrayList<ASTNode> nodes = PsiUtil.findDescendantElements(SQFPsiUtil.getContainingScope(astNode.getPsi()), SQFTypes.VARIABLE, astNode, var.getVarName());
					for (ASTNode node : nodes) {
						if (node == this.var.getNode()) {
							//yes. the found variable's scope is a container for this.var's scope
							stopped = true;
							if (astNode.getPsi() instanceof SQFVariable) {
								firstDeclaration = (SQFVariable) astNode.getPsi();
							} else if (astNode.getPsi().getParent() instanceof SQFVariable) {
								firstDeclaration = (SQFVariable) astNode.getPsi().getParent();
							} else {
								throw new RuntimeException("this shouldn't happen");
							}

						}
					}
				}
			}
		}

		@Override
		public boolean traverseFoundNodesChildren() {
			boolean temp = traverseFoundChildren; //reset for each node
			traverseFoundChildren = true;
			return temp;
		}

		@Override
		public boolean stopped() {
			return this.stopped;
		}

		public SQFVariable getFirstDeclaration() {
			return firstDeclaration;
		}
	}


	/**
	 * Get all the declared private variables for the given scope. This will not go deeper than the current scope.
	 *
	 * @param scope SQFScope
	 * @return list of all private variables for the given scope
	 */
	public static List<SQFPrivateDeclVar> getPrivateDeclaredVars(SQFScope scope) {
		ASTNode[] statements = scope.getNode().getChildren(TokenSet.create(SQFTypes.STATEMENT));
		List<SQFPrivateDeclVar> ret = new ArrayList<>();
		SQFStatement statement;
		for (int i = 0; i < statements.length; i++) {
			statement = (SQFStatement) statements[i].getPsi();
			if (statement.getPrivateDecl() != null) {
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
	public static List<SQFPrivateDeclVar> getPrivateDeclVars(SQFPrivateDecl decl) {
		//although a similar method is automatically generated from the parser, it won't return a valid list when grammar is like: private "_var";
		//The valid list is only generated when grammar is like: private["_var1"];
		//This method is a workaround.
		PsiElement[] children = decl.getChildren();
		List<SQFPrivateDeclVar> list = new ArrayList<>(children.length);
		for (PsiElement child : children) {
			if (child instanceof SQFPrivateDeclVar) {
				list.add((SQFPrivateDeclVar) child);
			}
		}
		return list;
	}

	public static String toString(SQFPrivateDeclVar var) {
		return "SQFPrivateDeclVar{" + var.getVarName() + "}";
	}

}
