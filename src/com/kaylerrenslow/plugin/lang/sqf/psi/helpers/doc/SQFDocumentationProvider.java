package com.kaylerrenslow.plugin.lang.sqf.psi.helpers.doc;

import com.intellij.lang.documentation.DocumentationProviderEx;
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
public class SQFDocumentationProvider extends DocumentationProviderEx{
	private static final String COMMAND_URL_PREFIX = Plugin.resources.getString("plugin.doc.sqf.commandURLPrefix");
	private static final String COMMAND_DOC_EXTERNAL_LINK_NOTIFICATION = Plugin.resources.getString("plugin.doc.sqf.command_doc_external_link_notification_string_format");
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
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			lst.add(getCommandUrl(element.getText()));
			return lst;
		}
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return generateCommandDoc(element.getText());
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		if (PsiUtil.isOfElementType(element, SQFTypes.COMMAND)){
			return element;
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		return context;
	}

	@Override
	@Nullable
	public PsiElement getCustomDocumentationElement(@NotNull final Editor editor, @NotNull final PsiFile file, @Nullable PsiElement contextElement) {
		if (PsiUtil.isOfElementType(contextElement, SQFTypes.COMMAND)){
			return contextElement;
		}
		return null;
	}

	@Override
	@Nullable
	public Image getLocalImageForElement(@NotNull PsiElement element, @NotNull String imageSpec) {
		return null;
	}

	private String generateCommandDoc(String commandName) {
		String commandURL = getCommandUrl(commandName);
		String doc = "";
		try{
			doc = String.format(COMMAND_DOC_EXTERNAL_LINK_NOTIFICATION, commandURL) + FileReader.getInstance().getText(COMMANDS_DOC_FILE_DIR + commandName);

		}catch(Exception e){
			e.printStackTrace();
		}
		return doc;
	}

	private String getCommandUrl(String commandName) {
		return COMMAND_URL_PREFIX + commandName;
	}

}
