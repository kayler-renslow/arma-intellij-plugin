package com.kaylerrenslow.a3plugin.lang.sqf.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
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
import com.kaylerrenslow.a3plugin.Plugin;
import com.kaylerrenslow.a3plugin.lang.sqf.SQFStatic;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVisitor;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.mixin.SQFCommandElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kayler
 * @since 12/30/2016
 */
public class CommandCamelCaseInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public HighlightDisplayLevel getDefaultLevel() {
		return HighlightDisplayLevel.WARNING;
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new InspectionVisitor(holder);
	}

	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return Plugin.resources.getString("lang.sqf.inspection.command_camel_case.display_name");
	}

	@Nls
	@NotNull
	@Override
	public String getGroupDisplayName() {
		return Plugin.resources.getString("lang.sqf.inspection.group.misc.group_display_name");
	}

	/**
	 * Used for checking if commands are camelCase
	 *
	 * @author Kayler
	 * @since 12/30/2016
	 */
	private static class InspectionVisitor extends SQFVisitor {
		private ProblemsHolder holder;

		InspectionVisitor(ProblemsHolder holder) {
			this.holder = holder;
		}

		@Override
		public void visitCommand(@NotNull SQFCommand o) {
			super.visitCommand(o);
			for (String command : SQFStatic.LIST_COMMANDS) {
				if (o.getText().equalsIgnoreCase(command)) {
					if (!o.getText().equals(command)) {
						holder.registerProblem(o, Plugin.resources.getString("lang.sqf.annotator.command_not_camel_case"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CamelCaseFixAction(o));
					}
					break;
				}
			}
		}
	}

	private static class CamelCaseFixAction extends IntentionAndQuickFixAction {
		private final SmartPsiElementPointer<SQFCommand> commandPointer;

		public CamelCaseFixAction(@NotNull SQFCommand command) {
			this.commandPointer = SmartPointerManager.getInstance(command.getProject()).createSmartPsiElementPointer(command);
		}

		@NotNull
		@Override
		public String getName() {
			return Plugin.resources.getString("lang.sqf.inspection.command_camel_case.quick_fix");
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
					SQFCommand commandElement = commandPointer.getElement();
					if (commandElement == null) {
						return;
					}
					for (String command : SQFStatic.LIST_COMMANDS) {
						if (command.equalsIgnoreCase(commandElement.getText())) {
							commandElement.replace(SQFPsiUtil.createElement(project, command, SQFCommandElement.class));
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
