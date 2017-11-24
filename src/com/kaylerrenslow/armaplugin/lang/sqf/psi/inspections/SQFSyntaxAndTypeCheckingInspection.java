package com.kaylerrenslow.armaplugin.lang.sqf.psi.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiStatement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiVisitor;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFStatement;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFSyntaxHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 11/22/2017
 */
public class SQFSyntaxAndTypeCheckingInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new SQFPsiVisitor() {
			@Override
			public void visitStatement(@NotNull SQFPsiStatement o) {
				super.visitStatement(o);
				if (!(o instanceof SQFStatement)) {
					return;
				}
				SQFSyntaxHelper.getInstance().checkSyntax((SQFStatement) o, holder);
			}
		};
	}

	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return SQFStatic.getSQFBundle().getString("Inspections.SyntaxAndTypeCheck.display-name");
	}

	@Nullable
	@Override
	public String getStaticDescription() {
		return "<html><body>Checks SQF code to ensure valid syntax and argument types.</body></html>";
	}
}
