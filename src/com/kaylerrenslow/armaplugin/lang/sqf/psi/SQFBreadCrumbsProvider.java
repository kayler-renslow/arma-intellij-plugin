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
		if (element instanceof SQFScope) {
			return true;
		}

		if (element instanceof SQFStatement) {
			return true;
		}
		if (element instanceof SQFArray) {
			return true;
		}
		if (element instanceof SQFCommandExpression) {
			return !(element.getParent() instanceof SQFExpressionStatement);
		}


		return false;
	}

	@Nullable
	@Override
	public Icon getElementIcon(@NotNull PsiElement element) {
		if (element instanceof SQFFileScope) {
			return ArmaPluginIcons.ICON_SQF;
		}
		if (element instanceof SQFCommandExpression || element instanceof SQFCaseStatement) {
			return ArmaPluginIcons.ICON_SQF_COMMAND;
		}
		if (element instanceof SQFExpressionStatement) {
			SQFExpressionStatement exprStatement = (SQFExpressionStatement) element;
			SQFExpression exprStatementExpr = exprStatement.getExpr();
			if (exprStatementExpr instanceof SQFCommandExpression) {
				return ArmaPluginIcons.ICON_SQF_COMMAND;
			}
		}
		return null;
	}

	@NotNull
	@Override
	public String getElementInfo(@NotNull PsiElement element) {
		if (element instanceof SQFFileScope) {
			return element.getContainingFile().getName();
		}
		if (element instanceof SQFScope) {
			return "{...}";
		}

		if (element instanceof SQFCommandExpression) {
			return ((SQFCommandExpression) element).getExprOperator().getText();
		}

		if (element instanceof SQFStatement) {
			SQFStatement statement = (SQFStatement) element;

			if (statement instanceof SQFCaseStatement) {
				return "case";
			}

			SQFControlStructure controlStructure = statement.getControlStructure();
			if (controlStructure == null) {
				if (statement instanceof SQFExpressionStatement) {
					SQFExpressionStatement exprStatement = (SQFExpressionStatement) statement;
					SQFExpression exprStatementExpr = exprStatement.getExpr();
					if (exprStatementExpr instanceof SQFCommandExpression) {
						SQFCommandExpression expr = (SQFCommandExpression) exprStatementExpr;
						return expr.getExprOperator().getText();
					} else if (exprStatementExpr instanceof SQFLiteralExpression) {
						return exprStatementExpr.getText();
					}
				}
				if (statement instanceof SQFAssignmentStatement) {
					return ((SQFAssignmentStatement) statement).getVar().getText() + " = ...";
				}
				return "<SQF Statement>";
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
			return "[...]";
		}
		return element.getText();
	}
}
