package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Kayler
 * @since 12/10/2017
 */
public class KeyConverter extends ResolvingConverter<SQFString> {

	@Override
	public boolean isReferenceTo(@NotNull PsiElement element, String stringValue, @Nullable SQFString resolveResult, ConvertContext context) {
		if (resolveResult == null) {
			return false;
		}
		if (element == resolveResult) {
			return true;
		}
		if (element.getText().equals(resolveResult.getText())) {
			return true;
		}
		return false;
	}

	@Nullable
	@Override
	public SQFString fromString(@Nullable String s, ConvertContext context) {
		return PsiUtil.createElement(context.getProject(), "\"" + s + "\"", SQFFileType.INSTANCE, SQFString.class);
	}

	@Nullable
	@Override
	public String toString(@Nullable SQFString string, ConvertContext context) {
		return string == null ? "" : string.getNonQuoteText();
	}

	@NotNull
	@Override
	public Collection<? extends SQFString> getVariants(ConvertContext context) {
		return Collections.emptyList();
	}
}
