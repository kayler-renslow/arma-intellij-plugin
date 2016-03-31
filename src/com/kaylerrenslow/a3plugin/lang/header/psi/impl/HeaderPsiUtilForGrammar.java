package com.kaylerrenslow.a3plugin.lang.header.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.a3plugin.lang.header.psi.*;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.util.Attribute;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         Created on 01/01/2016.
 */
public class HeaderPsiUtilForGrammar {
	public static String getClassName(HeaderClassDeclaration decl) {
		return getClassName(decl.getClassStub());
	}

	public static String getClassName(HeaderClassStub classStub) {
		ASTNode node = PsiUtil.getNextSiblingNotWhitespace(classStub.getFirstChild().getNode());
		return node.getText();
	}

	public static String getPathString(HeaderPreInclude include){
		return include.getPreIncludeFile().getText().substring(1, include.getPreIncludeFile().getText().length() - 1);
	}

	public static String getAssigningVariable(HeaderAssignment assignment){
		if(assignment instanceof HeaderBasicAssignment){
			return ((HeaderBasicAssignment)assignment).getAssignmentIdentifier().getText();
		}
		if(assignment instanceof HeaderArrayAssignment){
			return ((HeaderArrayAssignment)assignment).getAssignmentIdentifier().getText();
		}
		throw new RuntimeException("can't get assigning variable from assignment type " + assignment.getClass());
	}

	public static String getAssigningValue(HeaderAssignment assignment){
		return assignment.getText().substring(assignment.getText().indexOf('=') + 1).trim();
	}

	public static boolean hasAttributes(HeaderClassDeclaration classDeclaration, Attribute[] attributes, boolean traverseIncludes) {
		Attribute[] classAttributes = classDeclaration.getAttributes(traverseIncludes);
		for(int i = 0; i < classAttributes.length; i++){
			boolean matched = true;
			for(int j = 0; j < attributes.length; j++){
				if(!attributes[j].equals(classAttributes[i])){
					matched = false;
				}
			}
			if(!matched){
				return false;
			}
		}
		return attributes.length == 0;
	}

	public static Attribute[] getAttributes(HeaderClassDeclaration decl, boolean traverseIncludes) {
		ArrayList<Attribute> attributesList = new ArrayList<>();
		if(decl.getClassContent() != null){
			getAttributes(decl.getClassContent().getFileEntryList(), traverseIncludes, attributesList);
		}
		return attributesList.toArray(new Attribute[attributesList.size()]);
	}

	private static void getAttributes(List<HeaderFileEntry> fileEntryList, boolean traverseIncludes, ArrayList<Attribute> attributesList) {
		for(HeaderFileEntry entry : fileEntryList){
			if(entry.getPreprocessorGroup() != null && traverseIncludes){
				List<HeaderPreprocessor> processors = entry.getPreprocessorGroup().getPreprocessorList();
				for(HeaderPreprocessor preprocessor : processors){
					if(preprocessor instanceof HeaderPreInclude){
						HeaderFile includedFile = ((HeaderPreInclude) preprocessor).getHeaderFileFromInclude();
						if(includedFile != null){
							HeaderClassDeclaration firstClass = (HeaderClassDeclaration)PsiUtil.findFirstDescendantElement(includedFile, HeaderClassDeclaration.class);
							if(firstClass.getClassContent() != null){
								getAttributes(firstClass.getClassContent().getFileEntryList(), true, attributesList);
							}
						}
					}
				}
			}
			if(entry.getAssignment() != null){
				attributesList.add(new Attribute(entry.getAssignment().getAssigningVariable(), entry.getAssignment().getAssigningValue()));
			}
		}
	}

	/**
	 * Fetches the HeaderFile inside the include.
	 *
	 * @param include HeaderPreInclude instance
	 * @return HeaderFile that points to the file included, or null if the include's file path doesn't point to an existing file
	 */
	@Nullable
	public static HeaderFile getHeaderFileFromInclude(HeaderPreInclude include) {
		Project p = include.getProject();
		VirtualFile f = p.getProjectFile().findFileByRelativePath(include.getPathString());
		if (f == null) {
			return null;
		}
		return (HeaderFile) PsiManager.getInstance(p).findFile(f);
	}
}
