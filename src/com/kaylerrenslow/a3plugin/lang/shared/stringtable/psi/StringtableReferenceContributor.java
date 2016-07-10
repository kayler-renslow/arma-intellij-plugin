package com.kaylerrenslow.a3plugin.lang.shared.stringtable.psi;

import com.intellij.openapi.module.Module;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderPsiUtil;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderStringtableKey;
import com.kaylerrenslow.a3plugin.lang.header.psi.references.HeaderStringtableKeyReference;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.Stringtable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.project.ArmaProjectDataManager;
import com.kaylerrenslow.a3plugin.util.PluginUtil;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 Created by Kayler on 06/25/2016.
 */
public class StringtableReferenceContributor extends PsiReferenceContributor {
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlAttributeValue.class), new PsiReferenceProvider() {
			@NotNull
			@Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
				XmlAttributeValue attributeValue = (XmlAttributeValue) element;
				if (!(attributeValue.getParent() instanceof XmlAttribute && ((XmlAttribute) attributeValue.getParent()).getName().equals("ID"))) {
					return PsiReference.EMPTY_ARRAY;
				}
				Module m = PluginUtil.getModuleForPsiFile(element.getContainingFile());
				if (m == null) {
					return PsiReference.EMPTY_ARRAY;
				}
				try {
					Stringtable stringtable = ArmaProjectDataManager.getInstance().getDataForModule(m).getStringtable();
					if(element.getContainingFile() == null){
						return PsiReference.EMPTY_ARRAY;
					}
					if (!element.getContainingFile().getVirtualFile().equals(stringtable.getVirtualFile())) {
						return PsiReference.EMPTY_ARRAY;
					}
				} catch (FileNotFoundException e) {
					return PsiReference.EMPTY_ARRAY;
				}

				ArrayList<PsiReference> referenceList = new ArrayList<>();
				List<SQFString> stringList = SQFPsiUtil.findAllStrings(element.getProject(), m, attributeValue.getText());
				for (SQFString sqfString : stringList) {
					referenceList.add(new StringtableSQFStringReference(sqfString, attributeValue));
				}
				List<HeaderStringtableKey> headerStringKeyList = HeaderPsiUtil.findAllStringtableKeys(element.getProject(), m, attributeValue.getValue());
				for (HeaderStringtableKey key : headerStringKeyList) {
					referenceList.add(new HeaderStringtableKeyReference(key, attributeValue));
				}

				return referenceList.toArray(new PsiReference[referenceList.size()]);
			}
		});
	}
}
