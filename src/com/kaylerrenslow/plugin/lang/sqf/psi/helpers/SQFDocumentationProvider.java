package com.kaylerrenslow.plugin.lang.sqf.psi.helpers;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.kaylerrenslow.plugin.Plugin;
import com.kaylerrenslow.plugin.lang.sqf.psi.SQFTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created by Kayler on 01/03/2016.
 */
public class SQFDocumentationProvider implements DocumentationProvider{
	private static final String COMMAND_URL_PREFIX = Plugin.resources.getString("plugin.doc.commandURLPrefix");


	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return generateDoc(element, originalElement);
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		System.out.println(getClass() + " getURLFor");
		List<String> lst = Collections.emptyList();
		System.out.println(originalElement.getNode().getElementType());
		if(element.getNode().getElementType() == SQFTypes.COMMAND){
			lst.add(COMMAND_URL_PREFIX + element.getText());
			return lst;
		}

		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		System.out.println(getClass() + " generateDoc");
		System.out.println(element.getNode().getElementType());
		if(element.getNode().getElementType() == SQFTypes.COMMAND){
			return COMMAND_URL_PREFIX + element.getText();
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		System.out.println(getClass() + " getDocumentationElementForLookupItem");
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		System.out.println(getClass() + " getDocumentationElementForLink");
		return null;
	}
}
