package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kayler
 *         Psi utilities for SQF
 *         Created on 03/20/2016.
 */
public class SQFPsiUtil {

	/**
	 * Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 * <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 * <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>
	 *
	 * @param variable Variable to test
	 * @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(@NotNull String variable) {
		return variable.matches(SQFStatic.FUNCTION_NAMING_RULE_REGEX); //don't need to explicitly check if a number starts the variable name since that is asserted by the lexer
	}

	/**
	 * Checks if the given PsiElement is a BIS function (is of type SQFTypes.VARIABLE or SQFTypes.GLOBAL_VAR and text starts with BIS_fnc, false otherwise).
	 *
	 * @param element element
	 * @return true if the given PsiElement is a BIS function, false otherwise
	 */
	public static boolean isBisFunction(@NotNull PsiElement element) {
		if (PsiUtil.isOfElementType(element, SQFTypes.VARIABLE)) {
			if (((SQFVariable) element).getVarName().startsWith("BIS_fnc")) {
				return true;
			}
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.GLOBAL_VAR)) {
			if (element.getText().startsWith("BIS_fnc")) {
				return true;
			}
		}
		return false;
	}


	@NotNull
	public static SQFFileScope getFileScope(@NotNull SQFFile containingFile) {
		return (SQFFileScope) containingFile.getNode().getChildren(TokenSet.create(SQFTypes.FILE_SCOPE))[0].getPsi();
	}


	/**
	 * Gets the containing scope of the psi element. Scope is determined by what code block element is in, or if not in a code block then the file's file scope is returned
	 *
	 * @param element element to get scope of
	 * @return scope
	 */
	@NotNull
	public static SQFScope getContainingScope(@NotNull PsiElement element) {
		PsiElement parent = element;
		while (parent != null && !(parent instanceof SQFScope)) {
			parent = parent.getParent();
		}
		return (SQFScope) parent;
	}

	/**
	 * Adds all SQFVariables in the current module that is equal to findVar into a list and returns it
	 *
	 * @param project project
	 * @param findVar global variable
	 * @return list
	 */
	@NotNull
	public static List<SQFVariable> findGlobalVariables(@NotNull Project project, @NotNull SQFVariable findVar) {
		List<SQFVariable> result = new ArrayList<>();
		Module m = PluginUtil.getModuleForPsiFile(findVar.getContainingFile());
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, m.getModuleContentScope());
		for (VirtualFile virtualFile : files) {
			SQFFile sqfFile = (SQFFile) PsiManager.getInstance(project).findFile(virtualFile);
			if (sqfFile == null) {
				continue;
			}
			ArrayList<SQFVariable> vars = PsiUtil.<SQFVariable>findDescendantElementsOfInstance(sqfFile, SQFVariable.class, null);
			if (vars == null) {
				continue;
			}
			for (SQFVariable var : vars) {
				IElementType type = var.getVariableType();
				if (type != SQFTypes.GLOBAL_VAR) {
					continue;
				}
				if (findVar.getVarName().equals(var.getVarName())) {
					result.add(var);
				}
			}
		}
		return result;
	}

	/**
	 * Creates a new SQFPrivateDecl with new vars appended and returns it
	 *
	 * @param project  project
	 * @param decl     the private declaration to append to
	 * @param varNames the names of the new variables to put inside the declaration
	 * @return decl var
	 */
	@NotNull
	public static SQFPrivateDecl createPrivateDeclFromExisting(@NotNull Project project, @NotNull SQFPrivateDecl decl, @NotNull String... varNames) {
		List<SQFPrivateDeclVar> declVars = decl.getPrivateDeclVars();
		String text = "private[";
		for (SQFPrivateDeclVar declVar : declVars) {
			text += "\"" + declVar.getVarName() + "\",";
		}
		for (int i = 0; i < varNames.length; i++) {
			text += "\"" + varNames[i] + (i != varNames.length - 1 ? "\"," : "\"];");
		}
		return (SQFPrivateDecl) createElement(project, text, SQFTypes.PRIVATE_DECL);
	}

	@NotNull
	public static SQFPrivateDeclVar createPrivateDeclVarElement(@NotNull Project project, @NotNull String varName) {
		SQFFile file = createFile(project, "private \"" + varName + "\";");
		return (SQFPrivateDeclVar) PsiUtil.findDescendantElements(file, SQFTypes.PRIVATE_DECL_VAR, null).get(0).getPsi();
	}

	@NotNull
	public static SQFVariable createVariable(@NotNull Project project, @NotNull String text) {
		return (SQFVariable) createElement(project, text, SQFTypes.VARIABLE);
	}

	@NotNull
	public static SQFFile createFile(@NotNull Project project, @NotNull String text) {
		String fileName = "fake_sqf_file.sqf";
		return (SQFFile) PsiFileFactory.getInstance(project).createFileFromText(fileName, SQFFileType.INSTANCE, text);
	}

	@NotNull
	public static String getCommentContent(@NotNull PsiComment comment) {
		if (comment.getNode().getElementType() == SQFTypes.INLINE_COMMENT) {
			if (comment.getText().length() <= 2) {
				return "";
			}
			return comment.getText().substring(2);
		}
		return comment.getText().substring(2, comment.getTextLength() - 2).replaceAll("\t([^\r\n])", "$1"); //shift comments left 1 tab if tabbed
	}

	@NotNull
	public static PsiElement createElement(@NotNull Project project, @NotNull String text, @NotNull IElementType type) {
		SQFFile file = createFile(project, text);
		return PsiUtil.findDescendantElements(file, type, null).get(0).getPsi();
	}
}
