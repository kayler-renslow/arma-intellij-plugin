package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import com.kaylerrenslow.armaplugin.ArmaPluginIcons;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

/**
 * @author Kayler
 * @since 01/19/2018
 */
public class SQFBreadCrumbsProvider implements BreadcrumbsProvider {

	@Override
	public Language[] getLanguages() {
		return new Language[]{SQFLanguage.INSTANCE};
	}

	@Override
	public boolean acceptElement(@NotNull PsiElement element) {
		if (!(element.getContainingFile() instanceof SQFFile)) {
			return false;
		}

		if (element instanceof SQFFileScope) {
			return true;
		}
		if (element instanceof SQFStatement) {
			return true;
		}
		if (element instanceof SQFArray) {
			return true;
		}

		return false;
	}

	@Nullable
	@Override
	public Icon getElementIcon(@NotNull PsiElement element) {
		return ArmaPluginIcons.ICON_SQF;
	}

//	@NotNull
//	@Override
//	public List<PsiElement> getChildren(@NotNull PsiElement element) {
//		return Arrays.asList(element.getChildren());
//	}

	@NotNull
	@Override
	public String getElementInfo(@NotNull PsiElement element) {
		if (element instanceof SQFFileScope) {
			return element.getContainingFile().getName();
		}

		if (element instanceof SQFStatement) {
			SQFStatement statement = (SQFStatement) element;
			SQFControlStructure controlStructure = statement.getControlStructure();
			if (controlStructure == null) {
				return ";;";
			}
			if (controlStructure instanceof SQFForEachHelperStatement) {
				return "forEach";
			} else if (controlStructure instanceof SQFForLoopHelperStatement) {
				return "for";
			} else if (controlStructure instanceof SQFIfHelperStatement) {
				return "if";
			} else if (controlStructure instanceof SQFSwitchHelperStatement) {
				return "switch";
			} else if (controlStructure instanceof SQFWhileLoopHelperStatement) {
				return "while";
			} else {
				return "<ControlStructure>";
			}
		}
		if (element instanceof SQFArray) {

		}
		return element.getText();
	}
}
