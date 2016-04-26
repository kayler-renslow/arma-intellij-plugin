package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.StringtableLookupElementDataObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Kayler
 * This is mostly to get stringtable.xml documentation
 * Created on 04/25/2016.
 */
public class XMLDocumentationProvider implements DocumentationProvider {
	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		return null;
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
		if(element instanceof XmlTag){ //do not delete. this is necessary for SQF auto completion for localize "stringtableEntry"
			XmlTag tag = ((XmlTag)element);
			Boolean keyValue = tag.getUserData(StringtableLookupElementDataObject.KEY_IS_STRINGTABLE_XML);
			if(keyValue != null && keyValue){
				return Stringtable.getKeyDoc(tag);
			}
		}
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
		return null;
	}
}
