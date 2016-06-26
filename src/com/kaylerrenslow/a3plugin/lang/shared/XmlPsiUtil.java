package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 06/25/2016.
 */
public class XmlPsiUtil {
	public static <T extends PsiElement> T createXmlElement(@NotNull Project project, @NotNull String text, @NotNull Class<T> tClass) {
		XmlFile file = createFile(project, text);
		return PsiUtil.findFirstDescendantElement(file, tClass);
	}

	public static XmlFile createFile(@NotNull Project project, @NotNull String text) {
		String fileName = "fake_xml_file.xml";
		return (XmlFile) PsiFileFactory.getInstance(project).createFileFromText("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+fileName, XmlFileType.INSTANCE, text);
	}
}
