package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableInStringReference;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.reference.SQFVariableReference;
import com.kaylerrenslow.armaplugin.lang.sqf.syntax.CommandDescriptorCluster;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Kayler
 * @since 05/23/2017
 */
public abstract class SQFScope extends ASTWrapperPsiElement implements SQFSyntaxNode {

	public SQFScope(@NotNull ASTNode node) {
		super(node);
	}

	/**
	 * @return a list of {@link SQFStatement} instances that are direct children of this scope
	 */
	@NotNull
	public List<SQFStatement> getChildStatements() {
		List<SQFStatement> statements = new ArrayList<>();
		iterateStatements(statement -> {
			statements.add(statement);
		});
		return statements;
	}


	/**
	 * A way to iterate child {@link SQFStatement} without needing to allocate a list/array
	 *
	 * @param callback a callback that passes in a {@link SQFStatement}.
	 */
	public void iterateStatements(@NotNull Consumer<SQFStatement> callback) {
		List<SQFStatement> statements = new ArrayList<>();
		for (PsiElement element : getChildren()) {
			if (!(element instanceof SQFStatement)) {
				continue;
			}
			callback.accept((SQFStatement) element);
		}
	}

	@NotNull
	public String getTextNoNewlines() {
		return getText().replaceAll("\n", " ");
	}

	@NotNull
	@Override
	public Object accept(@NotNull SQFSyntaxVisitor visitor, @NotNull CommandDescriptorCluster cluster) {
		return visitor.visit(this, cluster);
	}

	@NotNull
	public static List<SQFVariableReference> getVariableReferencesFor(@NotNull SQFVariable variable) {
		List<SQFVariableReference> vars = new ArrayList<>();
		PsiFile file = variable.getContainingFile();
		if (file == null) {
			throw new IllegalArgumentException("variable doesn't have a containing file");
		}
		SQFScope fileScope = getContainingScope(file);

		List<SQFVariable> varTargets = new ArrayList<>();
		List<SQFString> stringTargets = new ArrayList<>();

		PsiUtil.traverseBreadthFirstSearch(fileScope.getNode(), astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (!(nodeAsPsi instanceof SQFVariable || nodeAsPsi instanceof SQFString)) {
				return false;
			}
			if (nodeAsPsi instanceof SQFVariable) {
				SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
				if (SQFVariableName.nameEquals(sqfVariable.getVarName(), variable.getVarName())) {
					varTargets.add(sqfVariable);
				} else {
					return false;
				}
			} else {
				SQFString string = (SQFString) nodeAsPsi;
				if (SQFVariableName.nameEquals(string.getNonQuoteText(), variable.getVarName())) {
					stringTargets.add(string);
				} else {
					return false;
				}
			}

			return false;
		});

		if (!varTargets.isEmpty()) {
			vars.add(new SQFVariableReference.IdentifierReference(variable, varTargets));
		}
		if (!stringTargets.isEmpty()) {
			vars.add(new SQFVariableReference.StringReference(variable, stringTargets));
		}
		return vars;
	}

	@NotNull
	public static List<SQFVariableInStringReference> getVariableReferencesFor(@NotNull SQFString string) {
		List<SQFVariableInStringReference> vars = new ArrayList<>();
		PsiFile file = string.getContainingFile();
		if (file == null) {
			throw new IllegalArgumentException("string doesn't have a containing file");
		}
		String varName = string.getNonQuoteText();
		SQFScope fileScope = getContainingScope(file);

		List<SQFVariable> varTargets = new ArrayList<>();

		PsiUtil.traverseBreadthFirstSearch(fileScope.getNode(), astNode -> {
			PsiElement nodeAsPsi = astNode.getPsi();
			if (!(nodeAsPsi instanceof SQFVariable)) {
				return false;
			}
			SQFVariable sqfVariable = (SQFVariable) nodeAsPsi;
			if (SQFVariableName.nameEquals(sqfVariable.getVarName(), varName)) {
				varTargets.add(sqfVariable);
			} else {
				return false;
			}

			return false;
		});

		if (!varTargets.isEmpty()) {
			vars.add(new SQFVariableInStringReference(string, varTargets));
		}
		return vars;
	}


	/**
	 * Gets the {@link SQFScope} for the given PsiElement. If the given element is an {@link SQFScope} instance,
	 * the first scope containing that scope will be returned.
	 * <p>
	 * If element is {@link SQFFileScope}, element will be returned and cast to {@link SQFScope};
	 * <p>
	 * If element is an {@link SQFFile}, the file's {@link SQFFileScope} will be returned
	 *
	 * @param element where to get the containing scope from
	 * @return the containing scope
	 * @throws IllegalArgumentException when element isn't in an {@link SQFFile}
	 */
	@NotNull
	public static SQFScope getContainingScope(@NotNull PsiElement element) {
		if (element instanceof SQFFile) {
			SQFFileScope fileScope = ((SQFFile) element).findChildByClass(SQFFileScope.class);
			if (fileScope == null) {
				throw new IllegalStateException("no SQFFileScope for file " + element);
			}
			return fileScope;
		}
		if (element instanceof PsiFile) {
			throw new IllegalArgumentException("element is a PsiFile, but not an SQFFile");
		}
		if (element instanceof SQFFileScope) {
			return (SQFScope) element;
		}
		PsiFile file = element.getContainingFile();
		if (!(file instanceof SQFFile)) {
			throw new IllegalArgumentException("element isn't in an SQF file");
		}
		PsiElement cursor = element;
		while (cursor != null) {
			cursor = cursor.getParent();
			if (cursor instanceof SQFScope) { //will also cover file scope
				return (SQFScope) cursor;
			}
		}
		SQFFileScope fileScope = ((SQFFile) file).findChildByClass(SQFFileScope.class);
		if (fileScope == null) {
			throw new IllegalStateException("no SQFFileScope for file " + file);
		}
		return fileScope;
	}

}
