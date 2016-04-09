package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * When find usages is called by user, each type of usage is classified as something like 'assignment' or 'function call'. This class handles the naming of found usages.
 * Created on 03/23/2016.
 */
public class ArmaUsageTypeProvider implements UsageTypeProvider{

	private static final UsageType TYPE_PRIVATE_DECL_VAR = new UsageType(Plugin.resources.getString("lang.shared.usage_type.private_decl_var"));
	private static final UsageType TYPE_UNKNOWN_HEADER_USAGE = new UsageType(Plugin.resources.getString("lang.shared.usage_type.unknown_header"));
	private static final UsageType TYPE_UNKNOWN_SQF_USAGE = new UsageType(Plugin.resources.getString("lang.shared.usage_type.unknown_sqf"));
	private static final UsageType TYPE_ASSIGNMENT = new UsageType(Plugin.resources.getString("lang.shared.usage_type.assignment"));
	private static final UsageType TYPE_FUNCTION_CALL = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_call"));
	private static final UsageType TYPE_FUNCTION_SPAWN = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_spawn"));
	private static final UsageType TYPE_FUNCTION_PARAMETER = new UsageType(Plugin.resources.getString("lang.shared.usage_type.function_param"));
	private static final UsageType TYPE_RETURN_STATEMENT = new UsageType(Plugin.resources.getString("lang.shared.usage_type.return_statement"));

	@Nullable
	@Override
	public UsageType getUsageType(PsiElement element) {
		if (element instanceof SQFVariable){

			UsageType x = getUsageTypeForVariable((SQFVariable)element);
			if (x != null){
				return x;
			}
		}
		if(PsiUtil.isOfElementType(element.getNode(), SQFTypes.PRIVATE_DECL_VAR)){
			return TYPE_PRIVATE_DECL_VAR;
		}

		if (element.getContainingFile() instanceof SQFFile){
			return TYPE_UNKNOWN_SQF_USAGE;
		}else if (element.getContainingFile() instanceof HeaderFile){
			return TYPE_UNKNOWN_HEADER_USAGE;
		}
		return null;
	}

	@Nullable
	private UsageType getUsageTypeForVariable(SQFVariable variable) {
		ASTNode node = PsiUtil.getAncestorWithType(variable.getNode(), SQFTypes.STATEMENT, null);
		if (node != null){
			SQFStatement statement = (SQFStatement) node.getPsi();
			if(statement.getAssignment() != null){
				SQFAssignment assignment = statement.getAssignment();
				if(assignment.getAssigningVariable() == variable){
					return TYPE_ASSIGNMENT;
				}
			}
		}

		node = PsiUtil.getPrevSiblingNotWhitespace(variable.getNode());
		if (PsiUtil.isOfElementType(node, SQFTypes.COMMAND) && node.getText().equals("call")){
			return TYPE_FUNCTION_CALL;
		}
		if (PsiUtil.isOfElementType(node, SQFTypes.COMMAND) && node.getText().equals("spawn")){
			return TYPE_FUNCTION_SPAWN;
		}

		if (PsiUtil.isDescendantOf(variable.getNode(), SQFTypes.RETURN_STATEMENT, null)){
			return TYPE_RETURN_STATEMENT;
		}

		/*Fall back to basic usage types to show the usages are still in Header files or SQF files*/
		node = PsiUtil.getAncestorWithType(variable.getNode(), SQFTypes.ARRAY_VAL, null);
		if (node != null){
			if (nodeIsFunctionParameter(node)){
				return TYPE_FUNCTION_PARAMETER;
			}
		}
		if (nodeIsFunctionParameter(variable.getNode())){
			return TYPE_FUNCTION_PARAMETER;
		}

		return null; //let intellij decide the name
	}

	private boolean nodeIsFunctionParameter(ASTNode node) {
		ASTNode sibling = PsiUtil.getNextSiblingNotWhitespace(node);
		return PsiUtil.isOfElementType(sibling, SQFTypes.COMMAND) && (sibling.getText().equals("call") || sibling.getText().equals("spawn"));
	}
}