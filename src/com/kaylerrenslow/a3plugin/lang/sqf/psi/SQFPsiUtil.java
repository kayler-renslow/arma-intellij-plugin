package com.kaylerrenslow.a3plugin.lang.sqf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFFileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kayler
 * Psi utilities for SQF
 * Created on 03/20/2016.
 */
public class SQFPsiUtil{

	/** Checks if the given PsiElement is a BIS function (is of type SQFTypes.VARIABLE or SQFTypes.GLOBAL_VAR and text starts with BIS_fnc, false otherwise).
	 * @param element element
	 * @return true if the given PsiElement is a BIS function, false otherwise
	 */
	public static boolean isBisFunction(PsiElement element){
		if(PsiUtil.isOfElementType(element, SQFTypes.VARIABLE)){
			if(((SQFVariable)element).getVarName().startsWith("BIS_fnc")){
				return true;
			}
		}
		if(PsiUtil.isOfElementType(element, SQFTypes.GLOBAL_VAR)){
			if(element.getText().startsWith("BIS_fnc")){
				return true;
			}
		}
		return false;
	}

	/** Gets the current scope of the variable. This is determined by where a private declaration with its name is scoped.
	 * @param var element to get scope of
	 * @return scope
	 */
	public static SQFScope getCurrentScopeForVariable(SQFVariable var){
		SQFScope scope = getCurrentScope(var);
		if(var.getVariableType() == SQFTypes.LANG_VAR){
			if(var.getVarName().equals("_this")){
				return getFileScope((SQFFile) var.getContainingFile());
			}
			return scope;
		}
		if(var.getVariableType() == SQFTypes.GLOBAL_VAR){
			return getFileScope((SQFFile) var.getContainingFile());
		}

		List<SQFPrivateDeclVar> varsForScope = getPrivateVarsForScope(scope);

		while(scope != null){
			if(scope instanceof SQFFileScope){
				break;
			}
			for (SQFPrivateDeclVar varInScope : varsForScope){
				if (varInScope.getVarName().equals(var.getVarName())){
					return scope;
				}
			}
			scope = getCurrentScope(scope.getParent());
			varsForScope = getPrivateVarsForScope(scope);
		}
		return scope;
	}

	public static SQFFileScope getFileScope(SQFFile containingFile) {
		return (SQFFileScope)containingFile.getNode().getChildren(TokenSet.create(SQFTypes.FILE_SCOPE))[0].getPsi();
	}

	public static List<SQFPrivateDeclVar> getPrivateVarsForScope(SQFScope scope){
		List<ASTNode> list = PsiUtil.findDescendantElements(scope, SQFTypes.PRIVATE_DECL_VAR, null);
		List<SQFPrivateDeclVar> ret = new ArrayList<>();
		for(int i = 0; i < list.size(); i++){
			ret.add((SQFPrivateDeclVar)list.get(i).getPsi());
		}
		return ret;
	}

	/** Gets the current scope of the psi element. Scope is determined by what code block element is in, or if not in a code block then the file's file scope is returned
	 * @param element element to get scope of
	 * @return scope
	 */
	public static SQFScope getCurrentScope(PsiElement element){
		PsiElement parent = element;
		while(parent != null && !(parent instanceof SQFScope)){
			parent = parent.getParent();
		}
		return (SQFScope) parent;
	}

	/** Adds all SQFVariables in the entire project that is equal to findVar into a list and returns it
	 * @param project project
	 * @param findVar variable text to look for
	 * @return list
	 */
	public static List<SQFVariable> findGlobalVariables(Project project, String findVar){
		List<SQFVariable> result = new ArrayList<>();
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, GlobalSearchScope.allScope(project));
		for(VirtualFile virtualFile : files){
			SQFFile sqfFile = (SQFFile) PsiManager.getInstance(project).findFile(virtualFile);
			if(sqfFile == null){
				continue;
			}
			SQFVariable[] vars = PsiTreeUtil.getChildrenOfType(sqfFile, SQFVariable.class);
			if(vars == null){
				continue;
			}
			for(SQFVariable var : vars){
				IElementType type = var.getVariableType();
				if(type != SQFTypes.GLOBAL_VAR){
					continue;
				}
				if(findVar == null){
					result.add(var);
				}else{
					if(findVar.equals(var.getText())){
						result.add(var);
					}
				}
			}
		}
		return result;
	}

	/** Adds all SQFVariables in the entire project into a list and returns it
	 * @param project project
	 * @return list
	 */
	public static List<SQFVariable> findGlobalVariables(Project project){
		return findGlobalVariables(project, null);
	}

	public static SQFPrivateDeclVar createPrivateDeclVarElement(Project project, String text){
		SQFFile file = createFile(project, text);
		return (SQFPrivateDeclVar) PsiUtil.findDescendantElements(file, SQFTypes.PRIVATE_DECL_VAR, null).get(0).getPsi();
	}

	public static SQFVariable createVariable(Project project, String text){
		SQFFile file = createFile(project, text);
		return (SQFVariable) PsiUtil.findDescendantElements(file, SQFTypes.VARIABLE, null).get(0).getPsi();
	}

	public static SQFFile createFile(Project project, String text){
		String fileName = "fake_sqf_file.sqf";
		return (SQFFile) PsiFileFactory.getInstance(project).createFileFromText(fileName, SQFFileType.INSTANCE, text);
	}

	public static String getCommentContent(PsiComment comment){
		if(comment.getNode().getElementType() == SQFTypes.INLINE_COMMENT){
			if(comment.getText().length() <= 2){
				return  "";
			}
			return comment.getText().substring(2).trim();
		}
		return comment.getText().substring(2, comment.getTextLength() - 2).trim();
	}

	public static PsiElement createElement(Project project, String text, IElementType type) {
		SQFFile file = createFile(project, text);
		return PsiUtil.findDescendantElements(file, type, null).get(0).getPsi();
	}
}
