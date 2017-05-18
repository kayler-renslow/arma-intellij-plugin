package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.util.Attribute;
import com.kaylerrenslow.a3plugin.util.FilePath;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import com.kaylerrenslow.a3plugin.util.TraversalObjectFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Psi utilities for methods inside the bnf grammar
 *
 * @author Kayler
 * @see HeaderPsiUtil
 * @since 01/01/2016
 */
public class HeaderPsiUtilForGrammar {

	public static String getKey(HeaderStringtableKey key) {
		return key.getNode().getText().substring(1).replaceFirst("[sS][tT][rR]_", "str_");
	}

	public static boolean bracesAreEmpty(HeaderClassDeclaration classDeclaration) {
		if (classDeclaration.getClassContent() == null) {
			return true;
		}
		HeaderClassContent content = classDeclaration.getClassContent();
		return content.getFileEntries().getFileEntryList().size() == 0;
	}

	public static void removeBracesIfEmpty(HeaderClassDeclaration classDeclaration) {
		if (classDeclaration.getClassContent() == null) {
			return;
		}
		HeaderClassContent content = classDeclaration.getClassContent();
		if (content.getFileEntries().getFileEntryList().size() == 0) {
			classDeclaration.getNode().removeChild(content.getNode());
		}
	}

	public static ASTNode getClassNameNode(HeaderClassDeclaration declaration) {
		return getClassNameNode(declaration.getClassStub());
	}

	public static ASTNode getClassNameNode(HeaderClassStub classStub) {
		return PsiUtil.getNextSiblingNotWhitespace(classStub.getFirstChild().getNode());
	}

	public static String getExtendClassName(HeaderClassDeclaration declaration) {
		return getExtendClassName(declaration.getClassStub());
	}

	public static String getExtendClassName(HeaderClassStub classStub) {
		if (classStub.getText().contains(":")) {
			int index = classStub.getText().indexOf(':') + 1;
			return classStub.getText().substring(index).trim();
		} else {
			return null;
		}
	}

	public static String getClassName(HeaderClassDeclaration decl) {
		return getClassName(decl.getClassStub());
	}

	public static String getClassName(HeaderClassStub classStub) {
		return getClassNameNode(classStub).getText();
	}

	public static String getPathString(HeaderPreInclude include) {
		return include.getPreIncludeFile().getText().substring(1, include.getPreIncludeFile().getText().length() - 1);
	}

	public static ASTNode getAssigningIdentifierNode(HeaderAssignment assignment) {
		if (assignment instanceof HeaderBasicAssignment) {
			return ((HeaderBasicAssignment) assignment).getAssignmentIdentifier().getNode();
		}
		if (assignment instanceof HeaderArrayAssignment) {
			return ((HeaderArrayAssignment) assignment).getAssignmentIdentifier().getNode();
		}
		throw new RuntimeException("can't get assigning variable from assignment type " + assignment.getClass());
	}

	public static String getAssigningVariable(HeaderAssignment assignment) {
		return assignment.getAssigningIdentifierNode().getText();
	}

	public static String getAssigningValue(HeaderAssignment assignment) {
		return assignment.getText().substring(assignment.getText().indexOf('=') + 1).trim();
	}

	public static boolean hasAttributes(HeaderClassDeclaration classDeclaration, Attribute[] attributes, boolean traverseIncludes) {
		Attribute[] classAttributes = classDeclaration.getAttributes(traverseIncludes);
		int matched = 0;
		for (int i = 0; i < classAttributes.length; i++) {
			for (int j = 0; j < attributes.length; j++) {
				if (attributes[j].equals(classAttributes[i])) {
					matched++;
				}
			}
		}
		if (matched == attributes.length) {
			return true;
		}
		return false;
	}

	public static HeaderClassDeclaration addClassDeclaration(HeaderClassDeclaration decl, String newClassDeclName, Attribute[] attributes) {
		HeaderFileEntry fileEntry = decl.addFileEntry(HeaderPsiUtil.createClassDeclarationText(newClassDeclName, attributes));
		return fileEntry.getClassDeclaration();
	}

	public static HeaderFileEntry addFileEntry(HeaderClassDeclaration decl, String textWithoutSemicolon) {
		decl.createClassContent();
		HeaderFileEntry fileEntry = (HeaderFileEntry) HeaderPsiUtil.createElement(decl.getProject(), textWithoutSemicolon + ";", HeaderTypes.FILE_ENTRY);
		decl.getClassContent().getFileEntries().getNode().addChild(fileEntry.getNode());
		return fileEntry;
	}

	public static void createClassContent(HeaderClassDeclaration decl) {
		if (decl.getClassContent() != null) {
			return;
		}
		decl.getNode().addChild(HeaderPsiUtil.createClassDeclaration(decl.getProject(), "temp", null).getClassContent().getNode());
	}

	public static void removeFromTree(HeaderClassDeclaration decl) {
		decl.getParent().getParent().getNode().removeChild(decl.getParent().getNode());
	}

	/**
	 * Get all attributes inside the class declaration. Attributes are any assignment inside the class. If traverseIncludes is true, it will traverse include statements
	 *
	 * @param traverseIncludes true if to traverse includes while getting attributes, false if to skip over them
	 * @return array of attributes (assignment expressions)
	 */
	public static Attribute[] getAttributes(HeaderClassDeclaration decl, boolean traverseIncludes) {
		ArrayList<Attribute> attributesList = new ArrayList<>();
		if (decl.getClassContent() != null) {
			getAttributes(decl.getClassContent().getFileEntries().getFileEntryList(), traverseIncludes, attributesList);
		}
		return attributesList.toArray(new Attribute[attributesList.size()]);
	}

	public static void setAttribute(HeaderClassDeclaration decl, String attribute, String newValue) {
		traverseFileEntries(decl.getClassContent().getFileEntries().getFileEntryList(), true, new TraversalObjectFinder<HeaderFileEntry>() {
			private boolean stopped;

			@Override
			public void found(@NotNull HeaderFileEntry headerFileEntry) {
				HeaderAssignment assignment = headerFileEntry.getAssignment();
				if (assignment.getAssigningVariable().equals(attribute)) {
					this.stopped = true;
					Project project = headerFileEntry.getProject();
					IElementType type = assignment.getText().contains("[]") ? HeaderTypes.ARRAY_ASSIGNMENT : HeaderTypes.BASIC_ASSIGNMENT;

					assignment.getParent().getNode().replaceChild(assignment.getNode(), HeaderPsiUtil.createElement(project, attribute + "=" + newValue + ";", type).getNode());
				}
			}

			@Override
			public boolean stopped() {
				return this.stopped;
			}

			@Override
			public boolean traverseFoundNodesChildren() {
				return true;
			}
		});
	}

	private static void getAttributes(List<HeaderFileEntry> fileEntryList, boolean traverseIncludes, ArrayList<Attribute> attributesList) {
		traverseFileEntries(fileEntryList, traverseIncludes, new TraversalObjectFinder<HeaderFileEntry>() {
			@Override
			public void found(@NotNull HeaderFileEntry headerFileEntry) {
				attributesList.add(new Attribute(headerFileEntry.getAssignment().getAssigningVariable(), headerFileEntry.getAssignment().getAssigningValue()));
			}

			@Override
			public boolean stopped() {
				return false;
			}

			@Override
			public boolean traverseFoundNodesChildren() {
				return true;
			}
		});
	}

	private static void traverseFileEntries(List<HeaderFileEntry> fileEntryList, boolean traverseIncludes, TraversalObjectFinder<HeaderFileEntry> entryFinder) {
		for (HeaderFileEntry entry : fileEntryList) {
			if (entry.getPreprocessorGroup() != null && traverseIncludes) {
				List<HeaderPreprocessor> processors = entry.getPreprocessorGroup().getPreprocessorList();
				for (HeaderPreprocessor preprocessor : processors) {
					if (preprocessor instanceof HeaderPreInclude) {
						PsiFile includedFile = ((HeaderPreInclude) preprocessor).getHeaderFileFromInclude();
						if (includedFile instanceof HeaderFile) {
							HeaderClassDeclaration firstClass = PsiUtil.findFirstDescendantElement(includedFile, HeaderClassDeclaration.class);
							if (firstClass == null) {
								return;
							}
							if (firstClass.getClassContent() != null) {
								if (entryFinder.stopped()) {
									return;
								}
								traverseFileEntries(firstClass.getClassContent().getFileEntries().getFileEntryList(), true, entryFinder);
							}
						}
					}
				}
			}
			if (entry.getAssignment() != null) {
				entryFinder.found(entry);
			}
			if (entryFinder.stopped()) {
				return;
			}
		}
	}

	/**
	 * Fetches the PsiFile inside the include.
	 *
	 * @param include HeaderPreInclude instance
	 * @return PsiFile that points to the file included, or null if the include's file path doesn't point to an existing file
	 */
	@Nullable
	public static PsiFile getHeaderFileFromInclude(HeaderPreInclude include) {
		FilePath path = FilePath.getFilePathFromString(include.getPathString(), FilePath.DEFAULT_DELIMETER);
		return PluginUtil.findFileByPath(path, include.getContainingFile().getContainingDirectory());
	}
}
