package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderClass;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.ArmaPluginUtil;
import com.kaylerrenslow.armaplugin.lang.DocumentationUtil;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.header.GenericConfigException;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunctionUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.*;
import com.kaylerrenslow.armaplugin.stringtable.StringTableKey;
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
		if (element == null) {
			return null;
		}
		if (element.getNode() == null) {
			return null;
		}
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
		if (element.getNode() == null) {
			return null;
		}
		if (element instanceof XmlTag) {
			return StringTableKey.getKeyDoc((XmlTag) element);
		}
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
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		if (object instanceof StringTableKey) {
			return ((StringTableKey) object).getIDXmlTag();
		}
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
			List<VirtualFile> rootConfigVirtFiles = ArmaPluginUtil.getConfigVirtualFiles(element);
			if (rootConfigVirtFiles.isEmpty()) {
				return null;
			}
			VirtualFile functionVirtFile = HeaderConfigFunctionUtil.locateConfigFunctionVirtualFile(rootConfigVirtFiles, function);
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
		if (link.startsWith(DOC_LINK_PREFIX_COMMAND)) {
			SQFFile file = PsiUtil.createFile(psiManager.getProject(), link.substring(DOC_LINK_PREFIX_COMMAND.length()), SQFFileType.INSTANCE);
			return PsiUtil.findFirstDescendantElement(file, SQFCommand.class);
		}
		if (link.startsWith(DOC_LINK_PREFIX_BIS_FUNCTION)) {
			SQFFile file = PsiUtil.createFile(psiManager.getProject(), link.substring(DOC_LINK_PREFIX_BIS_FUNCTION.length()), SQFFileType.INSTANCE);
			return PsiUtil.findFirstDescendantElement(file, SQFVariable.class);
		}
		if (link.startsWith(DOC_LINK_PREFIX_USER_FUNCTION)) {
			if (context == null) {
				return null;
			}
			String functionName = link.substring(DOC_LINK_PREFIX_USER_FUNCTION.length());
			List<HeaderFile> headerFiles = ArmaPluginUserData.getInstance().parseAndGetConfigHeaderFiles(context);
			if (headerFiles.isEmpty()) {
				return null;
			}
			HeaderClass cfgFunctions;
			HeaderConfigFunction function;

			List<VirtualFile> configVirtFiles = ArmaPluginUtil.getConfigVirtualFiles(context);
			if (configVirtFiles.isEmpty()) {
				return null;
			}

			for (HeaderFile headerFile : headerFiles) {
				try {
					cfgFunctions = HeaderConfigFunctionUtil.getCfgFunctions(headerFile);
					function = HeaderConfigFunctionUtil.getFunctionFromCfgFunctionsBody(new SQFVariableName(functionName), cfgFunctions);
					if (function == null) {
						continue;
					}
				} catch (GenericConfigException e) {
					continue;
				}
				VirtualFile functionVirtFile = HeaderConfigFunctionUtil.locateConfigFunctionVirtualFile(configVirtFiles, function);
				if (functionVirtFile == null) {
					continue;
				}
				PsiFile match = psiManager.findFile(functionVirtFile);
				if (match != null) {
					return match;
				}
			}
		}
		return null;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file,
													@Nullable PsiElement contextElement) {
		if (contextElement == null) {
			return null;
		}
		if (SQFParserDefinition.isCommand(contextElement.getNode().getElementType())) {
			return contextElement;
		}
		return null;
	}

	@NotNull
	private static String generateCommandDoc(@NotNull String commandName) {
		return getCommandDocumentation(commandName);
	}

	@NotNull
	private static String generateFunctionDoc(@NotNull String functionName) {
		return getBISFunctionDocumentation(functionName);
	}

	@NotNull
	public static String getCommandDocumentation(@NotNull String commandName) {
		try {
			return String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(commandName)) + FileResourceContentExtractor.extract(getDocumentationFilePath(commandName));
		} catch (IllegalArgumentException ignore) {
			for (String command : SQFStatic.COMMANDS_SET) {
				if (command.equalsIgnoreCase(commandName)) {
					return String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(command)) + FileResourceContentExtractor.extract(getDocumentationFilePath(command));
				}
			}

		}
		return "Error fetching documentation.";
	}

	@NotNull
	private static String getDocumentationFilePath(@NotNull String commandName) {
		return SQFStatic.COMMANDS_DOC_FILE_DIR + commandName;
	}

	@NotNull
	public static String getBISFunctionDocumentation(@NotNull String bisFunction) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(bisFunction));
		return doc + FileResourceContentExtractor.extract(SQFStatic.BIS_FUNCTIONS_DOC_FILE_DIR + bisFunction);
	}

	@NotNull
	public static String getWikiUrl(@NotNull String wikiLinkName) {
		return SQFStatic.BIS_WIKI_URL_PREFIX + wikiLinkName;
	}

	@NotNull
	private static final String EXTERNAL_LINK_NOTIFICATION = SQFStatic.getSQFBundle().getString("SQFStatic.external-wiki-link");
}
