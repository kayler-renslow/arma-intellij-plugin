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
import com.intellij.util.indexing.FileBasedIndex;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFFile;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFVariable;
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

			if (!var.isLocal()) {
				List<SQFVariable> vars = findGlobalVariables(element.getProject(), var);
				return new PsiReference[]{new SQFVariableReference.IdentifierReference(var, vars)};
			}
			return PsiReference.EMPTY_ARRAY;
		}

		/**
		 * Adds all SQFVariables in the current module that is equal to findVar into a list and returns it
		 *
		 * @param project project
		 * @param findVar global variable
		 * @return list
		 */
		@NotNull
		public static List<SQFVariable> findGlobalVariables(@NotNull Project project, @NotNull SQFVariable findVar) {
			List<SQFVariable> result = new ArrayList<>();
			Module m = ModuleUtil.findModuleForPsiElement(findVar);
			if (m == null) {
				return result;
			}
			GlobalSearchScope searchScope = m.getModuleContentScope();
			Collection<VirtualFile> files = FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, SQFFileType.INSTANCE, searchScope);
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
						if (!var.isLocal()) {
							if (SQFVariableName.nameEquals(var.getVarName(), findVar.getVarName())) {
								result.add(var);
							}
						}
					}
					return false;
				});
			}
			return result;
		}
	}

}
