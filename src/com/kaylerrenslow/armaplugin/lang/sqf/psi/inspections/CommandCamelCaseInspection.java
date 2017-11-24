package com.kaylerrenslow.armaplugin.lang.sqf.psi.inspections;

import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFFileType;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFPsiVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Inspection for finding when a command isn't in camelCase. This inspection will also provide a quick fix
 * that will make the command camelCase.
 *
 * @author Kayler
 * @since 12/30/2016
 */
public class CommandCamelCaseInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new InspectionVisitor(holder);
	}

	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return SQFStatic.getSQFBundle().getString("Inspections.CommandCamelCase.display-name");
	}

	@Nullable
	@Override
	public String getStaticDescription() {
		return "<html><body>Checks SQF commands to see if they are camelCase. If one is not, a quickfix will be available to change it to camelCase.</body></html>";
	}

	/**
	 * Used for checking if commands are camelCase
	 *
	 * @author Kayler
	 * @since 12/30/2016
	 */
	private static class InspectionVisitor extends SQFPsiVisitor {
		private ProblemsHolder holder;

		InspectionVisitor(ProblemsHolder holder) {
			this.holder = holder;
		}

		@Override
		public void visitCommand(@NotNull SQFPsiCommand o) {
			super.visitCommand(o);
			for (String command : SQFStatic.LIST_COMMANDS) {
				if (o.getText().equalsIgnoreCase(command)) {
					if (!o.getText().equals(command)) {
						holder.registerProblem(
								o, SQFStatic.getSQFBundle().getString("Inspections.CommandCamelCase.annotator-problem-description"),
								ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
								new CamelCaseFixAction(o)
						);
					}
					break;
				}
			}
		}
	}

	private static class CamelCaseFixAction extends IntentionAndQuickFixAction {
		private final SmartPsiElementPointer<SQFPsiCommand> commandPointer;

		public CamelCaseFixAction(@NotNull SQFPsiCommand command) {
			this.commandPointer = SmartPointerManager.getInstance(command.getProject()).createSmartPsiElementPointer(command);
		}

		@NotNull
		@Override
		public String getName() {
			return SQFStatic.getSQFBundle().getString("Inspections.CommandCamelCase.quickfix");
		}

		@NotNull
		@Override
		public String getFamilyName() {
			return "";
		}

		@Override
		public void applyFix(@NotNull Project project, PsiFile file, @Nullable Editor editor) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SQFPsiCommand commandElement = commandPointer.getElement();
					if (commandElement == null) {
						return;
					}
					for (String command : SQFStatic.LIST_COMMANDS) {
						if (command.equalsIgnoreCase(commandElement.getText())) {
							SQFPsiCommand c = PsiUtil.createElement(project, command, SQFFileType.INSTANCE, SQFPsiCommand.class);
							if (c == null) {
								return;
							}
							commandElement.replace(c);
							return;
						}
					}
					throw new IllegalStateException("command '" + commandElement.getText() + "' should have been matched");

				}
			};
			WriteCommandAction.runWriteCommandAction(project, runnable);
		}
	}
}