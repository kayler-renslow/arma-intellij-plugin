package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderClassDeclaration;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.DocumentationUtil;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringTable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringTableLookupElementDataObject;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides documentation for SQF PsiElements
 *
 * @author Kayler
 * @since 01/03/2016
 */
public class SQFDocumentationProvider extends DocumentationProviderEx {

	/**
	 * Used for BIS function documentation links. This comes after the psi element protocol in anchor tags. Example use case: &lt;a href='psi-element://bis-function:BIS_fnc_MP'&gt;BIS_fnc_MP&lt;/a&gt;
	 */
	public static final String DOC_LINK_PREFIX_BIS_FUNCTION = "bis-function:";
	/**
	 * Used for command documentation links. This comes after the psi element protocol in anchor tags. Example use case: &lt;a href='psi-element://command:createVehicle'&gt;createVehicle&lt;/a&gt;
	 */
	public static final String DOC_LINK_PREFIX_COMMAND = "command:";
	/**
	 * Used for CfgFunction documentation links. This comes after the psi element protocol in anchor tags. For functions defined in CfgFunctions. Example use case: &lt;a href='psi-element://function:f_fnc_test'&gt;f_fnc_test&lt;/a&gt;
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
		if (SQFStatic.isCommand(element.getNode().getElementType()) || SQFStatic.isBisFunction(element.getText())) {
			lst.add(SQFStatic.getWikiUrl(element.getText()));
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
		if (element instanceof XmlTag) {
			return StringTable.getKeyDoc((XmlTag) element);
		}
		if (SQFStatic.isCommand(element.getNode().getElementType())) {
			return generateCommandDoc(element.getText());
		}
		if (SQFStatic.isBisFunction(element.getText())) {
			return generateFunctionDoc(element.getText());
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(element, SQFTypes.BLOCK_COMMENT)) {
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
		if (element instanceof HeaderClassDeclaration) {
			return element.getText().replaceAll("[\t]+", " ");
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		if (object instanceof StringTableLookupElementDataObject) {
			return ((StringTableLookupElementDataObject) object).getTargetTag();
		}
		if (SQFStatic.isCommand(element.getNode().getElementType())) {
			return element;
		}
		if (element instanceof PsiFile) {
			return element;
		}
		if (object instanceof HeaderConfigFunction) {
			HeaderConfigFunction function = (HeaderConfigFunction) object;
			if (!function.getFunctionFileExtension().equals(".sqf")) {
				return function.getClassDeclaration();
			}
			return function.getPsiFile();

		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		try {
			if (link.startsWith(DOC_LINK_PREFIX_COMMAND)) {
				return PsiUtil.getFirstDescendantNode(SQFPsiUtil.createFile(context.getProject(), link.substring(DOC_LINK_PREFIX_COMMAND.length()))).getPsi();
			}
		} catch (Exception e) { //for when the commands are inside the documentation but not registered as a SQFTypes.COMMAND because lexer is not up to date
			e.printStackTrace(System.out);
		}
		if (link.startsWith(DOC_LINK_PREFIX_BIS_FUNCTION)) {
			return SQFPsiUtil.createElement(context.getProject(), link.substring(DOC_LINK_PREFIX_BIS_FUNCTION.length()), SQFTypes.GLOBAL_VAR);
		}
		if (link.startsWith(DOC_LINK_PREFIX_USER_FUNCTION)) {
			try {
				Module module = PluginUtil.getModuleForPsiFile(context.getContainingFile());
				if (module == null) {
					return null;
				}
				String functionName = link.substring(DOC_LINK_PREFIX_USER_FUNCTION.length());
				HeaderConfigFunction function = HeaderPsiUtil.getFunctionFromCfgFunctions(module, functionName);
				if (function != null) {
					if (!function.getFunctionFileExtension().equals(".sqf")) {
						return function.getClassDeclaration();
					}
					return function.getPsiFile();
				}
			} catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
		return null;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if (contextElement == null) {
			return null;
		}
		if (SQFStatic.isCommand(contextElement.getNode().getElementType())) {
			return contextElement;
		}

		if (PsiUtil.isOfElementType(contextElement, SQFTypes.LOCAL_VAR)) { //this code works, but only when the selected statement has the comment
			SQFVariable var = (SQFVariable) (contextElement.getParent());
			PsiReference[] references = var.getReferences();
			for (PsiReference reference : references) {
				if (reference.getElement().getParent() instanceof SQFAssignment) {
					SQFStatement statement = (SQFStatement) reference.getElement().getParent().getParent();
					PsiElement comment = getInlineComment(editor, statement.getNode());
					if (comment != null) {
						return comment;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private PsiElement getInlineComment(@NotNull Editor editor, ASTNode statementNode) {
		ASTNode commentNode = PsiUtil.getNextSiblingNotWhitespace(statementNode);
		if (PsiUtil.isOfElementType(commentNode, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(commentNode, SQFTypes.BLOCK_COMMENT)) {
			if (editor.getDocument().getLineNumber(statementNode.getStartOffset()) != editor.getDocument().getLineNumber(commentNode.getStartOffset())) { //comment not on same line
				return null;
			}
			return commentNode.getPsi();
		}
		return null;
	}

	private static String generateCommandDoc(String commandName) {
		return SQFStatic.getCommandDocumentation(commandName);
	}

	private static String generateFunctionDoc(String functionName) {
		return SQFStatic.getBISFunctionDocumentation(functionName);

	}

}
