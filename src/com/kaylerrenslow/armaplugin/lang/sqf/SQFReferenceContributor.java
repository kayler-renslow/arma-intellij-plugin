package com.kaylerrenslow.armaplugin.lang.sqf;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFCommand;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFFile;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFCommandReference;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kayler
 * @since 09/14/2017
 */
public class SQFReferenceContributor extends PsiReferenceContributor {
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(SQFVariable.class), new SQFGlobalVariableReferenceProvider());
		registrar.registerReferenceProvider(PlatformPatterns.psiElement(SQFCommand.class), new SQFCommandReferenceProvider());
	}

	/**
	 * This reference provider is meant for getting all {@link SQFCommand} instances across all files and creating a reference between them.
	 *
	 * @author Kayler
	 * @since 9/18/2017
	 */
	private static class SQFCommandReferenceProvider extends PsiReferenceProvider {

		@NotNull
		@Override
		public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			if (!(element instanceof SQFCommand)) {
				return PsiReference.EMPTY_ARRAY;
			}
			SQFCommand command = (SQFCommand) element;
			List<SQFCommand> commandInstances = findAllCommandInstances(element.getProject(), command);
			if (commandInstances.isEmpty()) {
				return PsiReference.EMPTY_ARRAY;
			}

			return new PsiReference[]{new SQFCommandReference(command, commandInstances)};
		}

		/**
		 * Adds all {@link SQFCommand} instances in the current module that is equal to findCommand into a list and returns it
		 *
		 * @param project     project
		 * @param findCommand the command
		 * @return list
		 */
		@NotNull
		public static List<SQFCommand> findAllCommandInstances(@NotNull Project project, @NotNull SQFCommand findCommand) {
			List<SQFCommand> result = new ArrayList<>();
			Module m = ModuleUtil.findModuleForPsiElement(findCommand);
			if (m == null) {
				return result;
			}
			GlobalSearchScope searchScope = m.getModuleContentScope();
			Collection<VirtualFile> files = FileTypeIndex.getFiles(SQFFileType.INSTANCE, searchScope);
			for (VirtualFile virtualFile : files) {
				PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
				if (!(file instanceof SQFFile)) {
					continue;
				}
				SQFFile sqfFile = (SQFFile) file;
				PsiUtil.traverseBreadthFirstSearch(sqfFile.getNode(), astNode -> {
					PsiElement nodeAsElement = astNode.getPsi();
					if (nodeAsElement instanceof SQFCommand) {
						SQFCommand command = (SQFCommand) nodeAsElement;
						if (command.commandNameEquals(findCommand.getCommandName())) {
							result.add(command);
						}
					}
					return false;
				});
			}
			return result;
		}
	}

	/**
	 * This reference provider is meant for getting all global variables across all files and creating a reference between them.
	 *
	 * @author Kayler
	 * @since 9/14/2017
	 */
	private static class SQFGlobalVariableReferenceProvider extends PsiReferenceProvider {

		@NotNull
		@Override
		public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			if (!(element instanceof SQFVariable)) {
				return PsiReference.EMPTY_ARRAY; //can't be referenced
			}
			SQFVariable var = (SQFVariable) element;

			if (var.isLocal()) {
				return PsiReference.EMPTY_ARRAY;
			}
			List<SQFVariable> vars = findGlobalVariables(element.getProject(), var);
			if (vars.isEmpty()) {
				return PsiReference.EMPTY_ARRAY;
			}
			return new PsiReference[]{new SQFVariableReference.IdentifierReference(var, vars)};
		}

		/**
		 * Adds all {@link SQFVariable}s in the current module that is equal to findVar into a list and returns it
		 * <p>
		 * If findVar is a local variable, the list returned will be empty.
		 *
		 * @param project project
		 * @param findVar variable
		 * @return list
		 */
		@NotNull
		public static List<SQFVariable> findGlobalVariables(@NotNull Project project, @NotNull SQFVariable findVar) {
			List<SQFVariable> result = new ArrayList<>();
			if (findVar.isLocal()) {
				return result;
			}
			Module m = ModuleUtil.findModuleForPsiElement(findVar);
			if (m == null) {
				return result;
			}
			GlobalSearchScope searchScope = m.getModuleContentScope();
			Collection<VirtualFile> files = FileTypeIndex.getFiles(SQFFileType.INSTANCE, searchScope);
			for (VirtualFile virtualFile : files) {
				PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
				if (!(file instanceof SQFFile)) {
					continue;
				}
				SQFFile sqfFile = (SQFFile) file;
				PsiUtil.traverseBreadthFirstSearch(sqfFile.getNode(), astNode -> {
					PsiElement nodeAsElement = astNode.getPsi();
					if (nodeAsElement instanceof SQFVariable) {
						SQFVariable var = (SQFVariable) nodeAsElement;
						if (var.isLocal()) {
							return false;
						}
						if (SQFVariableName.nameEquals(var.getVarName(), findVar.getVarName())) {
							result.add(var);
						}
					}
					return false;
				});
			}
			return result;
		}
	}

}
