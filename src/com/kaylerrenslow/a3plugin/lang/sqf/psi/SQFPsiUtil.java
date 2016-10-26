package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDecl;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 @author Kayler
 Psi utilities for SQF
 Created on 03/20/2016. */
public class SQFPsiUtil {


	/**
	 Check if the given element is inside a [] spawn {}. For spawn, all variables are created in a different environment
	 We must iterate over all upper code blocks and see if they are part of a spawn statement. Unlike scope.checkIfSpawn(), this will traverse the tree upwards to the file scope to make sure that the element isn't inside the spawn scope

	 @return the scope that starts the spawn scope ([] spawn {}), or null if not inside spawn
	 */
	@Nullable
	public static SQFScope checkIfInsideSpawn(@NotNull PsiElement element) {
		SQFScope containingScope = SQFPsiUtil.getContainingScope(element);

		PsiElement codeBlock = containingScope.getParent();
		SQFScope spawnScope = containingScope;
		ASTNode previous;
		while (codeBlock instanceof SQFCodeBlock) {
			previous = PsiUtil.getPrevSiblingNotWhitespace(codeBlock.getNode());
			if (PsiUtil.isOfElementType(previous, SQFTypes.COMMAND)) {
				if (previous.getText().equals("spawn")) {
					return spawnScope;
				}
			}
			spawnScope = SQFPsiUtil.getContainingScope(codeBlock);
			codeBlock = spawnScope.getParent();
		}
		return null;
	}

	/**
	 Checks if the given PsiElement is a BIS function (is of type SQFTypes.VARIABLE or SQFTypes.GLOBAL_VAR and is defined in documentation, false otherwise).

	 @param element element
	 @return true if the given PsiElement is a BIS function, false otherwise
	 */
	public static boolean isBisFunction(@NotNull PsiElement element) {
		if (PsiUtil.isOfElementType(element, SQFTypes.VARIABLE)) {
			return SQFStatic.isBisFunction(((SQFVariable) element).getVarName());
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.GLOBAL_VAR)) {
			SQFStatic.isBisFunction(element.getText());
		}
		return false;
	}


	@NotNull
	public static SQFFileScope getFileScope(@NotNull SQFFile containingFile) {
		return (SQFFileScope) containingFile.getNode().getChildren(TokenSet.create(SQFTypes.FILE_SCOPE))[0].getPsi();
	}


	/**
	 Gets the containing scope of the psi element. Scope is determined by what code block element is in, or if not in a code block then the file's file scope is returned

	 @param element element to get scope of
	 @return scope
	 */
	@NotNull
	public static SQFScope getContainingScope(@NotNull PsiElement element) {
		PsiElement parent = element.getParent();
		while (parent != null && !(parent instanceof SQFScope)) {
			parent = parent.getParent();
		}
		if (parent == null) {
			return getFileScope((SQFFile) element.getContainingFile());
		}
		return (SQFScope) parent;
	}

	/**
	 Adds all SQFVariables in the current module that is equal to findVar into a list and returns it

	 @param project project
	 @param findVar global variable
	 @return list
	 */
	@NotNull
	public static List<SQFVariable> findGlobalVariables(@NotNull Project project, @NotNull SQFVariable findVar) {
		List<SQFVariable> result = new ArrayList<>();
		Module m = PluginUtil.getModuleForPsiFile(findVar.getContainingFile());
		if (m == null) {
			return result;
		}
		GlobalSearchScope searchScope = m.getModuleContentScope();
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, searchScope);
		for (VirtualFile virtualFile : files) {
			PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
			if (!(file instanceof SQFFile)) {
				continue;
			}
			SQFFile sqfFile = (SQFFile) file;
			ArrayList<SQFVariable> vars = PsiUtil.findDescendantElementsOfInstance(sqfFile, SQFVariable.class, null);
			if (vars == null) {
				continue;
			}
			for (SQFVariable var : vars) {
				if (!var.isGlobalVariable()) {
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
	 Finds all SQFStrings in the module in all SQF files with text equal to quoteText

	 @param project project
	 @param module current module
	 @param quoteText text to search for (including quotes)
	 @return list all strings
	 */
	@NotNull
	public static List<SQFString> findAllStrings(@NotNull Project project, @NotNull Module module, @NotNull String quoteText) {
		List<SQFString> result = new ArrayList<>();
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, module.getModuleContentScope());
		for (VirtualFile virtualFile : files) {
			PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
			if (!(file instanceof SQFFile)) {
				continue;
			}
			SQFFile sqfFile = (SQFFile) file;
			ArrayList<SQFString> strings = PsiUtil.findDescendantElementsOfInstance(sqfFile, SQFString.class, null);
			if (strings == null) {
				continue;
			}
			for (SQFString string : strings) {
				if (string.getText().equals(quoteText)) {
					result.add(string);
				}
			}
		}
		return result;
	}

	/**
	 Adds all SQFVariables in the current module where the variable name is a config function and the function tag is equal to parameter tag

	 @param module module
	 @param tag tag to search for
	 @return list
	 */
	@NotNull
	public static List<SQFVariable> findConfigFunctionVariablesWithTag(@NotNull Module module, @NotNull String tag) {
		List<SQFVariable> result = new ArrayList<>();
		GlobalSearchScope searchScope = module.getModuleContentScope();
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, searchScope);
		for (VirtualFile virtualFile : files) {
			PsiFile file = PsiManager.getInstance(module.getProject()).findFile(virtualFile);
			if (!(file instanceof SQFFile)) {
				continue;
			}
			SQFFile sqfFile = (SQFFile) file;
			ArrayList<SQFVariable> vars = PsiUtil.<SQFVariable>findDescendantElementsOfInstance(sqfFile, SQFVariable.class, null);
			if (vars == null) {
				continue;
			}
			for (SQFVariable var : vars) {
				IElementType type = var.getVariableType();
				if (type != SQFTypes.GLOBAL_VAR) {
					continue;
				}
				if (SQFStatic.followsSQFFunctionNameRules(var.getVarName())) {
					SQFStatic.SQFFunctionTagAndName tagAndName = SQFStatic.getFunctionTagAndName(var.getVarName());
					if (tagAndName.tagName.equals(tag)) {
						result.add(var);
					}
				}
			}
		}
		return result;
	}

	/**
	 Creates a new SQFCommandExpression that is a private[] syntax with new vars appended and returns it

	 @param project project
	 @param decl the private declaration to append to
	 @param varNames the names of the new variables to put inside the declaration
	 @return command expression that is the new private declaration
	 */
	@NotNull
	public static SQFCommandExpression createPrivateDeclFromExisting(@NotNull Project project, @NotNull SQFPrivateDecl decl, @NotNull String... varNames) {
		List<SQFPrivateDeclVar> declVars = decl.getPrivateVars();
		String text = "private [";
		for (SQFPrivateDeclVar declVar : declVars) {
			text += "\"" + declVar.getVarName() + "\",";
		}
		for (int i = 0; i < varNames.length; i++) {
			text += "\"" + varNames[i] + (i != varNames.length - 1 ? "\"," : "\"];");
		}
		return (SQFCommandExpression) createElement(project, text, SQFTypes.COMMAND_EXPRESSION);
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
	public static PsiElement createElement(@NotNull Project project, @NotNull String text, @NotNull IElementType type) {
		SQFFile file = createFile(project, text);
		return PsiUtil.findDescendantElements(file, type, null).get(0).getPsi();
	}

	public static SQFString createNewStringLiteral(Project project, String textWithoutQuotes) {
		return (SQFString) createElement(project, "\"" + textWithoutQuotes + "\"", SQFTypes.STRING);
	}

	@SuppressWarnings("unchecked")
	/** Get all array entries in the array where the expression type is of class type. Do not make this a utility method for the grammar because the generic type parameter won't work */
	public static <T extends SQFExpression> List<T> getExpressionsOfType(SQFArrayVal array, Class<T> type) {
		List<SQFArrayEntry> arrayEntryList = array.getArrayEntryList();
		List<T> items = new ArrayList<>(arrayEntryList.size());
		for (SQFArrayEntry arrayEntry : arrayEntryList) { //iterate over each string in params ["_var1","_var2"]
			if (arrayEntry.getExpression() != null) {
				if (type.isInstance(arrayEntry.getExpression())) {
					items.add((T) arrayEntry.getExpression());
				}
			}
		}
		return items;
	}

	/**
	 Get a postfix argument of class type. This method will traverse as far right until the end of the expression (for instance, searching expression 'vehicle player setPos' will search all the way to setPos)<br>
	 Do not

	 @param commandExpression command expression
	 @return the first element of matched type, or null if none could be found.
	 */
	@Nullable
	public static <T extends PsiElement> T getAPostfixArgument(SQFCommandExpression commandExpression, Class<T> tClass) {
		PsiElement postfix = commandExpression.getPostfixArgument();
		while (postfix != null) {
			if (tClass.isInstance(postfix)) {
				return (T) postfix;
			}
			if (postfix instanceof SQFCommandExpression) {
				postfix = ((SQFCommandExpression) postfix).getPostfixArgument();
			} else {
				return null;
			}
		}
		return null;
	}
}
