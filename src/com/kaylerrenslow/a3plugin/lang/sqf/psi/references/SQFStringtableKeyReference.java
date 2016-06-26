package com.kaylerrenslow.a3plugin.lang.sqf.psi.references;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import com.kaylerrenslow.a3plugin.lang.header.psi.HeaderFile;
import com.kaylerrenslow.a3plugin.lang.shared.PsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 @author Kayler
 Creates a reference that points from SQF string to stringtable key attribute
 Created on 06/25/2016. */
public class SQFStringtableKeyReference implements PsiReference {
	private final SQFString string;
	private final XmlAttributeValue keyAttributeValue;

	public SQFStringtableKeyReference(SQFString string, XmlAttributeValue keyAttributeValue) {
		this.string = string;
		this.keyAttributeValue = keyAttributeValue;
	}

	@Override
	public PsiElement getElement() {
		return string;
	}

	@Override
	public TextRange getRangeInElement() {
		return string.getNonQuoteRangeRelativeToElement();
	}

	@Nullable
	@Override
	public PsiElement resolve() {
		return keyAttributeValue;
	}

	@NotNull
	@Override
	public String getCanonicalText() {
		SQFString resolve = (SQFString) resolve();
		if (resolve == null) {
			throw new RuntimeException("u wot m8");
		}
		return resolve.getNonQuoteText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
		XmlAttributeValue value = createXmlElement(this.keyAttributeValue.getProject(), newElementName, XmlAttributeValue.class);
		this.keyAttributeValue.getParent().getNode().replaceChild(keyAttributeValue.getNode(), value.getNode());
		return value;
	}

	private static <T extends PsiElement> T createXmlElement(@NotNull Project project, @NotNull String text, @NotNull Class<T> tClass) {
		HeaderFile file = createFile(project, text);
		return PsiUtil.findFirstDescendantElement(file, tClass);
	}

	private static HeaderFile createFile(@NotNull Project project, @NotNull String text) {
		String fileName = "fake_xml_file.xml";
		return (HeaderFile) PsiFileFactory.getInstance(project).createFileFromText(fileName, XmlFileType.INSTANCE, text);
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element) {
		return element == resolve();
	}

	@NotNull
	@Override
	public Object[] getVariants() {
		return new Object[0];
	}

	@Override
	public boolean isSoft() {
		return false;
	}
}

