package com.kaylerrenslow.a3plugin.lang.header.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.a3plugin.lang.header.ConfigClassNotDefinedException;
import com.kaylerrenslow.a3plugin.lang.header.FunctionNotDefinedInConfigException;
import com.kaylerrenslow.a3plugin.lang.header.HeaderFileType;
import com.kaylerrenslow.a3plugin.lang.header.psi.impl.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.util.Attribute;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kayler
 *         Created on 03/30/2016.
 */
public class HeaderPsiUtil {


	/**
	 * Gets the containing class of the given header element or null if it is undetermined.
	 * <p>It is undetermined if the containing class may be a sub class that is in another file that has headerElement's file included.
	 * For instance, all functions inside CfgFunctions have a containing class (CfgFunctions).
	 * But if CfgFunctions has an include statement inside, each included file has classes inside and those classes are technically subclasses of CfgFunctions.
	 * Finding the containing class from those included classes are therefore undetermined.</p>
	 *
	 * @param headerElement PsiElement that is from a HeaderFile
	 * @return containing class
	 */
	@Nullable
	public static HeaderClassDeclaration getContainingClass(PsiElement headerElement) {
		if (!(headerElement.getContainingFile() instanceof HeaderFile)) {
			throw new IllegalArgumentException("header element is not in a header file. it is in file type =" + headerElement.getContainingFile().getClass());
		}
		PsiElement parent = headerElement.getParent();
		while (parent != null) {
			if (parent instanceof HeaderClassDeclaration) {
				return (HeaderClassDeclaration) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Gets all class declarations where the given attributes are inside that class. If className is not null, class declarations will be added if it contains the given attributes and className=classDeclaration.className
	 *
	 * @param start            PsiElement to start at to get class declarations from
	 * @param className        class name to search for, or null if doesn't matter
	 * @param attributes       array of attributes to match
	 * @param traverseIncludes true if the includes inside the HeaderFile should be traversed, false if to ignore them
	 * @return list of all matched class declarations
	 */
	@NotNull
	public static ArrayList<HeaderClassDeclaration> getClassDeclarationsWithEntriesEqual(PsiElement start, @Nullable String className, Attribute[] attributes, boolean traverseIncludes) {
		ArrayList<HeaderClassDeclaration> matchedClasses = new ArrayList<>();
		getClassDeclarationsWithEntriesEqual(start, className, attributes, traverseIncludes, matchedClasses);
		return matchedClasses;
	}

	private static void getClassDeclarationsWithEntriesEqual(PsiElement start, @Nullable String className, @Nullable Attribute[] attributes, boolean traverseIncludes, ArrayList<HeaderClassDeclaration> matchedClasses) {
		ArrayList<PsiElement> fileEntries = PsiUtil.findDescendantElementsOfInstance(start, HeaderFileEntry.class, null);
		HeaderClassDeclaration classDecl;
		HeaderPreInclude include;
		for (PsiElement element : fileEntries) {
			if (element instanceof HeaderClassDeclaration) {
				classDecl = (HeaderClassDeclaration) element;
				if (className == null || classDecl.getClassName().equals(className)) {
					if (attributes == null || classDecl.hasAttributes(attributes, traverseIncludes)) {
						matchedClasses.add(classDecl);
					}

				}
			} else if (traverseIncludes && element instanceof HeaderPreprocessorGroup) {
				HeaderPreprocessorGroup group = (HeaderPreprocessorGroup)element;
				List<HeaderPreprocessor> preprocessors = group.getPreprocessorList();
				for(HeaderPreprocessor preprocessor : preprocessors){
					if(preprocessor instanceof HeaderPreInclude){
						include = (HeaderPreInclude) preprocessor;
						HeaderFile f = include.getHeaderFileFromInclude();
						if (f != null) {
							getClassDeclarationsWithEntriesEqual(f, className, attributes, true, matchedClasses);
						}
					}
				}
			}
		}
	}


	/**
	 * Traverses the PsiElement startElement to find the config class with its name=className
	 *
	 * @param startElement     where to start searching for a class declaration with the class declaration's name=className
	 * @param className        class name to search for
	 * @param traverseIncludes true if it should traverse the include statements inside the config file, false if it should search only the given config file
	 * @return HeaderClassDeclaration
	 * @throws ConfigClassNotDefinedException if the class is not defined in configFile (or if traverseIncludes==true and it still couldn't be found)
	 */
	@NotNull
	public static HeaderClassDeclaration getClassDeclaration(PsiElement startElement, @NotNull String className, boolean traverseIncludes) throws ConfigClassNotDefinedException {
		ArrayList<HeaderClassDeclaration> decls = getClassDeclarationsWithEntriesEqual(startElement, className, null, traverseIncludes);

		if (decls.size() == 0) {
			throw new ConfigClassNotDefinedException(className);
		}
		return decls.get(0);
	}


	/**
	 * Gets all included HeaderFiles included inside the given HeaderFile
	 *
	 * @param root root HeaderFile
	 * @return list of all included files
	 */
	@NotNull
	public static ArrayList<HeaderFile> getAllIncludedHeaderFilesInFile(HeaderFile root) {
		ArrayList<HeaderFile> includedFiles = new ArrayList<>();
		getAllIncludedHeaderFilesInFile(root, includedFiles);
		return includedFiles;
	}

	@NotNull
	private static void getAllIncludedHeaderFilesInFile(HeaderFile root, ArrayList<HeaderFile> includedFiles) {
		ArrayList<ASTNode> includeStatements = PsiUtil.findDescendantElements(root, HeaderTypes.PRE_INCLUDE, null);
		HeaderPreInclude include;
		for (ASTNode node : includeStatements) {
			include = (HeaderPreInclude) node.getPsi();
			HeaderFile f = include.getHeaderFileFromInclude();
			if (f != null) {
				includedFiles.add(f);
			}
		}
		for (HeaderFile headerFile : includedFiles) {
			getAllIncludedHeaderFilesInFile(headerFile, includedFiles);
		}
	}

	/**
	 * Creates a new HeaderConfigFunction instance for the given functionName
	 *
	 * @param psiFile HeaderFile instance to retrieve the description.ext for
	 * @param functionName full function name (example: tag_fnc_functionClassName )
	 * @return new HeaderConfigFunction instance for the given function name
	 * @throws ConfigClassNotDefinedException      when CfgFunctions is not defined in description.ext
	 * @throws FileNotFoundException               when there is no description.ext
	 * @throws FunctionNotDefinedInConfigException when the function isn't defined anywhere in CfgFunctions in description.ext
	 */
	public static HeaderConfigFunction getFunctionFromCfgFunctions(PsiFile psiFile, String functionName) throws ConfigClassNotDefinedException, FileNotFoundException, FunctionNotDefinedInConfigException {
		HeaderClassDeclaration cfgFuncs = getCfgFunctions(psiFile);
		int fnc = functionName.indexOf("_fnc_");
		String tagName = functionName.substring(0, fnc);
		String functionClassName = functionName.substring(fnc + 5);

		Attribute[] tagsAsAttributes = {new Attribute("tag", tagName)};
		ArrayList<HeaderClassDeclaration> classDeclarationMatched = (getClassDeclarationsWithEntriesEqual(cfgFuncs, null, tagsAsAttributes, true));

		HeaderClassDeclaration classWithTag;
		if (classDeclarationMatched.size() == 0) {
			try {
				classWithTag = getClassDeclaration(cfgFuncs, tagName, true);
			} catch (ConfigClassNotDefinedException e) {
				e.printStackTrace();
				throw new FunctionNotDefinedInConfigException(functionName);
			}
		} else {
			classWithTag = classDeclarationMatched.get(0);
		}

		/*At this point, classWithTag has to be defined. There was either a match from getClassDeclaration, getClassDeclarationsWithEntriesEqual, or an exception occurred prior to this point*/
		HeaderClassDeclaration functionClass;
		try {
			functionClass = getClassDeclaration(classWithTag, functionClassName, true);
		} catch (ConfigClassNotDefinedException e) {
			e.printStackTrace();
			throw new IllegalStateException("something REAAALY broke here");
		}
		String containingDirectoryPath = null;
		HeaderAssignment potentialFilePath = (HeaderAssignment) PsiUtil.findFirstDescendantElement(functionClass, HeaderBasicAssignment.class);
		if (potentialFilePath != null) { //file path is inside function's class declaration body. Example: functionClassName{file='somePath'};
			containingDirectoryPath = potentialFilePath.getAssigningValue();
		} else {
			//file path is determined by parent class
			HeaderClassDeclaration containingClassForFunctionClass = getContainingClass(functionClass);
			if (containingClassForFunctionClass != null) {
				Attribute[] attributes = containingClassForFunctionClass.getAttributes(true);
				boolean filePathDefined = false;
				for (Attribute attribute : attributes) {
					if (attribute.name.equals("file")) {
						containingDirectoryPath = attribute.value;
						filePathDefined = true;
						break;
					}
				}
				if (!filePathDefined) {
					containingDirectoryPath = "functions/" + containingClassForFunctionClass.getClassName();
				}
			} else {
				throw new FunctionNotDefinedInConfigException(functionName);
			}
		}

		return new HeaderConfigFunction(functionClass, containingDirectoryPath, tagName, functionClassName);
	}


	/**
	 * Get's the class declaration to CfgFunctions inside description.ext
	 *
	 * @param psiFile HeaderFile instance to retrieve the description.ext for
	 * @return class declaration
	 * @throws FileNotFoundException          when description.ext doesn't exist
	 * @throws ConfigClassNotDefinedException where CfgFunctions isn't defined inside description.ext
	 */
	public static HeaderClassDeclaration getCfgFunctions(PsiFile psiFile) throws FileNotFoundException, ConfigClassNotDefinedException {
		VirtualFile descriptionExt = getDescriptionExt(psiFile);
		HeaderFile file = (HeaderFile) PsiManager.getInstance(psiFile.getProject()).findFile(descriptionExt);
		return getClassDeclaration(file, "CfgFunctions", true);
	}

	/**
	 * Get a VirtualFile instance that points to the mission's description.ext
	 *
	 * @param psiFile HeaderFile instance to retrieve the description.ext for
	 * @return virtual file
	 * @throws FileNotFoundException when description.ext doesn't exist
	 */
	public static VirtualFile getDescriptionExt(PsiFile psiFile) throws FileNotFoundException {
		Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, HeaderFileType.INSTANCE, PluginUtil.getModuleSearchScope(psiFile));
		for (VirtualFile file : files) {
			if (file.getName().equalsIgnoreCase("description.ext")) {
				return file;
			}
		}
		throw new FileNotFoundException("Description.ext doesn't exist");

	}


}
