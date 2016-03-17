package com.kaylerrenslow.plugin.lang.sqf.psi.helpers.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.lang.psiUtil.PsiUtil;
import com.kaylerrenslow.plugin.lang.sqf.psi.SQFTypes;
import com.kaylerrenslow.plugin.util.FileReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayler on 01/03/2016.
 */
public class SQFDocumentationProvider extends AbstractDocumentationProvider{
	private static final String COMMAND_URL_PREFIX = Plugin.resources.getString("plugin.doc.sqf.commandURLPrefix");
	private static final String SEE_MORE_AT = Plugin.resources.getString("plugin.doc.sqf.seeMoreAt");
	private static final String COMMANDS_DOC_FILE_DIR = Plugin.resources.getString("plugin.doc.sqf.commandsDocFolder");


	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return generateDoc(element, originalElement);
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		List<String> lst = new ArrayList<>();
		if(PsiUtil.isOfElementType(originalElement, SQFTypes.COMMAND)){
			lst.add(COMMAND_URL_PREFIX + originalElement.getText());
			return lst;
		}
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if(PsiUtil.isOfElementType(originalElement, SQFTypes.COMMAND)){
			return generateCommandDoc(element.getText());
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		System.out.println(getClass() + " getDocumentationElementForLookupItem");
		return element;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		System.out.println(getClass() + " getDocumentationElementForLink");
		return context;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if(PsiUtil.isOfElementType(contextElement, SQFTypes.COMMAND)){
			return contextElement;
		}
		return null;
	}

	@Override
	@Nullable
	public Image getLocalImageForElement(@NotNull PsiElement element, @NotNull String imageSpec) {
//		System.out.println("SQFDocumentationProvider.getLocalImageForElement");
		return null;
	}

	private String generateCommandDoc(String commandName){
		String commandURL = getCommandUrl(commandName);
		String doc = FileReader.getInstance().getText(COMMANDS_DOC_FILE_DIR + commandName) + "<br><br><br>"+ SEE_MORE_AT + " <a href='" + commandURL + "'>"+commandURL+"</a>";
		return doc;
	}

	private String getCommandUrl(String commandName) {
		return COMMAND_URL_PREFIX + commandName;
	}

}
