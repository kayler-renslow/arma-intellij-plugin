package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.kaylerrenslow.armaplugin.lang.DocumentationUtil;
import com.kaylerrenslow.armaplugin.lang.PluginUtil;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;
import com.kaylerrenslow.armaplugin.util.FileResourceContentExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 09/06/2017
 */
public class SQFDocumentationProvider extends DocumentationProviderEx {
	/**
	 * Used for BIS function documentation links. This comes after the psi element protocol in anchor tags.
	 * Example use case: &lt;a href='psi-element://bis-function:BIS_fnc_MP'&gt;BIS_fnc_MP&lt;/a&gt;
	 */
	public static final String DOC_LINK_PREFIX_BIS_FUNCTION = "bis-function:";
	/**
	 * Used for command documentation links. This comes after the psi element protocol in anchor tags.
	 * Example use case: &lt;a href='psi-element://command:createVehicle'&gt;createVehicle&lt;/a&gt;
	 */
	public static final String DOC_LINK_PREFIX_COMMAND = "command:";
	/**
	 * Used for CfgFunction documentation links. This comes after the psi element protocol in anchor tags.
	 * For functions defined in CfgFunctions.
	 * Example use case: &lt;a href='psi-element://function:f_fnc_test'&gt;f_fnc_test&lt;/a&gt;
	 */
	public static final String DOC_LINK_PREFIX_USER_FUNCTION = "function:";

	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return generateDoc(element, originalElement);
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		List<String> lst = new ArrayList<>();
		if (SQFParserDefinition.isCommand(element.getNode().getElementType()) || SQFStatic.isBisFunction(element.getText())) {
			lst.add(SQFStatic.BIS_WIKI_URL_PREFIX + element.getText());
			return lst;
		}
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if (element == null) {
			return null;
		}
//		if (element instanceof XmlTag) {
//			return StringTable.getKeyDoc((XmlTag) element);
//		}
		if (SQFParserDefinition.isCommand(element.getNode().getElementType())) {
			return generateCommandDoc(element.getText());
		}
		if (SQFStatic.isBisFunction(element.getText())) {
			return generateFunctionDoc(element.getText());
		}
		if (PsiUtil.isOfElementType(element, SQFParserDefinition.INLINE_COMMENT) || PsiUtil.isOfElementType(element, SQFParserDefinition.BLOCK_COMMENT)) {
			PsiComment comment = (PsiComment) element;
			return DocumentationUtil.purtify(DocumentationUtil.getCommentContent(comment));
		}
		if (element instanceof PsiFile) {
			PsiElement[] children = element.getChildren();
			for (PsiElement child : children) {
				if (child instanceof SQFScope) {
					break;
				}
				if (child instanceof PsiComment) {
					return DocumentationUtil.purtify(DocumentationUtil.getCommentContent((PsiComment) child));
				}
			}
			return null;
		}
//		if (element instanceof HeaderClassDeclaration) {
//			return element.getText().replaceAll("[\t]+", " ");
//		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//		if (object instanceof StringTableLookupElementDataObject) {
//			return ((StringTableLookupElementDataObject) object).getTargetTag();
//		}
		if (SQFParserDefinition.isCommand(element.getNode().getElementType())) {
			return element;
		}
		if (element instanceof PsiFile) {
			return element;
		}
		if (object instanceof HeaderConfigFunction) {
			HeaderConfigFunction function = (HeaderConfigFunction) object;
			if (!function.getFunctionFileExtension().equals(".sqf")) {
				return null;
			}
//			String url = "";
//			try {
//				URL urlobj = new URL(new URL("file:"), function.getFullRelativePath());
//				url = urlobj.toString();
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
			VirtualFile descExtVirtFile = PluginUtil.getDescriptionExtVirtualFile(psiManager.getProject(), element);
			if (descExtVirtFile == null) {
				return null;
			}
			VirtualFile functionVirtFile = descExtVirtFile.findFileByRelativePath(function.getFullRelativePath());
			if (functionVirtFile == null) {
				return null;
			}
			return psiManager.findFile(functionVirtFile);
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		try {
			if (link.startsWith(DOC_LINK_PREFIX_COMMAND)) {
				SQFFile file = PsiUtil.createFile(psiManager.getProject(), link.substring(DOC_LINK_PREFIX_COMMAND.length()), SQFFileType.INSTANCE);
				return PsiUtil.findFirstDescendantElement(file, SQFCommand.class);
			}
		} catch (Exception e) { //for when the commands are inside the documentation but not registered as a SQFTypes.COMMAND because lexer is not up to date
			e.printStackTrace(System.out);
		}
		if (link.startsWith(DOC_LINK_PREFIX_BIS_FUNCTION)) {
			SQFFile file = PsiUtil.createFile(psiManager.getProject(), link.substring(DOC_LINK_PREFIX_BIS_FUNCTION.length()), SQFFileType.INSTANCE);
			return PsiUtil.findFirstDescendantElement(file, SQFVariable.class);
		}
		if (link.startsWith(DOC_LINK_PREFIX_USER_FUNCTION)) {
//			try {
//				Module module = PluginUtil.getModuleForPsiFile(context.getContainingFile());
//				if (module == null) {
//					return null;
//				}
//				String functionName = link.substring(DOC_LINK_PREFIX_USER_FUNCTION.length());
//				HeaderConfigFunction function = HeaderPsiUtil.getFunctionFromCfgFunctions(module, new SQFVariableName(functionName));
//				if (function != null) {
//					if (!function.getFunctionFileExtension().equals(".sqf")) {
//						return function.getClassDeclaration();
//					}
//					return function.getPsiFile();
//				}
//			} catch (Exception e) {
//				e.printStackTrace(System.out);
//			}
		}
		return null;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if (contextElement == null) {
			return null;
		}
		if (SQFParserDefinition.isCommand(contextElement.getNode().getElementType())) {
			return contextElement;
		}

		if (PsiUtil.isOfElementType(contextElement, SQFTypes.LOCAL_VAR)) { //this code works, but only when the selected statement has the comment
			SQFVariable var = (SQFVariable) (contextElement.getParent());
			PsiReference[] references = var.getReferences();
			for (PsiReference reference : references) {
//				if (reference.getElement().getParent() instanceof SQFAssignment) {
//					SQFStatement statement = (SQFStatement) reference.getElement().getParent().getParent();
//					PsiElement comment = getInlineComment(editor, statement.getNode());
//					if (comment != null) {
//						return comment;
//					}
//				}
			}
		}

		return null;
	}

	@Nullable
	private PsiElement getInlineComment(@NotNull Editor editor, ASTNode statementNode) {
		ASTNode commentNode = PsiUtil.getNextSiblingNotWhitespace(statementNode);
		if (PsiUtil.isOfElementType(commentNode, SQFParserDefinition.INLINE_COMMENT) || PsiUtil.isOfElementType(commentNode, SQFParserDefinition.BLOCK_COMMENT)) {
			if (editor.getDocument().getLineNumber(statementNode.getStartOffset()) != editor.getDocument().getLineNumber(commentNode.getStartOffset())) { //comment not on same line
				return null;
			}
			return commentNode.getPsi();
		}
		return null;
	}

	private static String generateCommandDoc(String commandName) {
		return getCommandDocumentation(commandName);
	}

	private static String generateFunctionDoc(String functionName) {
		return getBISFunctionDocumentation(functionName);
	}

	public static String getCommandDocumentation(String commandName) {
		try {
			return String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(commandName)) + FileResourceContentExtractor.extract(getDocumentationFilePath(commandName));
		} catch (IllegalArgumentException ignore) {
			for (String command : SQFStatic.LIST_COMMANDS) {
				if (command.equalsIgnoreCase(commandName)) {
					return String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(command)) + FileResourceContentExtractor.extract(getDocumentationFilePath(command));
				}
			}

		}
		return "Error fetching documentation.";
	}

	private static String getDocumentationFilePath(String commandName) {
		return SQFStatic.COMMANDS_DOC_FILE_DIR + commandName;
	}

	public static String getBISFunctionDocumentation(String bisFunction) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(bisFunction));
		return doc + FileResourceContentExtractor.extract(SQFStatic.BIS_FUNCTIONS_DOC_FILE_DIR + bisFunction);
	}

	public static String getWikiUrl(String wikiLinkName) {
		return SQFStatic.BIS_WIKI_URL_PREFIX + wikiLinkName;
	}

	@NotNull
	private static final String EXTERNAL_LINK_NOTIFICATION = SQFStatic.getSQFBundle().getString("SQFStatic.external-wiki-link");
}
