package com.kaylerrenslow.a3plugin.lang.sqf.providers;

import com.intellij.lang.ASTNode;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderConfigFunction;
import com.kaylerrenslow.a3plugin.lang.shared.DocumentationUtil;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringtableLookupElementDataObject;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.util.FileReader;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 *         Provides documentation for SQF PsiElements
 *         Created on 01/03/2016.
 */
public class SQFDocumentationProvider extends DocumentationProviderEx{
	private static final String BIS_WIKI_URL_PREFIX = Plugin.resources.getString("plugin.doc.sqf.wiki_URL_prefix");
	private static final String EXTERNAL_LINK_NOTIFICATION = Plugin.resources.getString("plugin.doc.sqf.wiki_doc_external_link_notification_string_format");
	private static final String DOC_LINK_PREFIX_BIS_FUNCTION = "bis-function:";
	private static final String DOC_LINK_PREFIX_COMMAND = "command:";

	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return generateDoc(element, originalElement);
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		List<String> lst = new ArrayList<>();
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND) || SQFStatic.isBisFunction(element.getText())){
			lst.add(getWikiUrl(element.getText()));
			return lst;
		}
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if(element instanceof XmlTag){
			return Stringtable.getKeyDoc((XmlTag)element);
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return generateCommandDoc(element.getText());
		}
		if(SQFStatic.isBisFunction(element.getText())){
			return generateFunctionDoc(element.getText());
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(element, SQFTypes.BLOCK_COMMENT)){
			PsiComment comment = (PsiComment) element;
			return DocumentationUtil.purtify(SQFPsiUtil.getCommentContent(comment));
		}
		if (element instanceof PsiFile){
			PsiElement[] children = element.getChildren();
			for(PsiElement child: children){
				if(child instanceof SQFScope){
					break;
				}
				if(child instanceof PsiComment){
					return DocumentationUtil.purtify(SQFPsiUtil.getCommentContent((PsiComment)child));
				}
			}
			return null;
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		if(object instanceof StringtableLookupElementDataObject){
			return ((StringtableLookupElementDataObject)object).getTargetTag();
		}
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return element;
		}
		if (element instanceof PsiFile){
			return element;
		}
		if(object instanceof HeaderConfigFunction){
			HeaderConfigFunction function = (HeaderConfigFunction)object;
			if(!function.getFunctionFileExtension().equals(".sqf")){
				return function.getClassDeclaration();
			}
			VirtualFile functionFile = PluginUtil.findFileInModule(function.getFullRelativePath(), PluginUtil.getModuleForPsiFile(function.getClassDeclaration().getContainingFile()),SQFFileType.INSTANCE);
			if(functionFile != null){
				return psiManager.findFile(functionFile);
			}
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		try{
			if (link.startsWith(DOC_LINK_PREFIX_COMMAND)){
				return SQFPsiUtil.createElement(context.getProject(), link.substring(DOC_LINK_PREFIX_COMMAND.length()), SQFTypes.COMMAND);
			}
		}catch(IndexOutOfBoundsException e){ //for when the commands are inside the documentation but not registered as a SQFTypes.COMMAND because lexer is not up to date
			e.printStackTrace(System.out);
		}
		if(link.startsWith(DOC_LINK_PREFIX_BIS_FUNCTION)){
			return SQFPsiUtil.createElement(context.getProject(), link.substring(DOC_LINK_PREFIX_BIS_FUNCTION.length()), SQFTypes.GLOBAL_VAR);
		}
		return null;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if (PsiUtil.isOfElementType(contextElement, SQFTypes.COMMAND)){
			return contextElement;
		}

		if (PsiUtil.isOfElementType(contextElement, SQFTypes.LOCAL_VAR)){ //this code works, but only when the selected statement has the comment
			SQFVariable var = (SQFVariable) (contextElement.getParent());
			PsiReference[] references = var.getReferences();
			for (PsiReference reference : references){
				if (reference.getElement().getParent() instanceof SQFAssignment){
					SQFStatement statement = (SQFStatement) reference.getElement().getParent().getParent();
					PsiElement comment = getInlineComment(editor, statement.getNode());
					if (comment != null){
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
		if (PsiUtil.isOfElementType(commentNode, SQFTypes.INLINE_COMMENT) || PsiUtil.isOfElementType(commentNode, SQFTypes.BLOCK_COMMENT)){
			if (editor.getDocument().getLineNumber(statementNode.getStartOffset()) != editor.getDocument().getLineNumber(commentNode.getStartOffset())){ //comment not on same line
				return null;
			}
			return commentNode.getPsi();
		}
		return null;
	}

	private static String generateCommandDoc(String commandName) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(commandName));
		try{
			doc += FileReader.getText(SQFStatic.COMMANDS_DOC_FILE_DIR + commandName);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		return doc;
	}

	private static String generateFunctionDoc(String functionName) {
		String doc = String.format(EXTERNAL_LINK_NOTIFICATION, getWikiUrl(functionName));
		try{
			doc += FileReader.getText(SQFStatic.BIS_FUNCTIONS_DOC_FILE_DIR + functionName);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		return doc;
	}

	private static String getWikiUrl(String wikiLinkName) {
		return BIS_WIKI_URL_PREFIX + wikiLinkName;
	}

}
