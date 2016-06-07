package com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFPsiUtil;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFParamsStatement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateAssignmentPrivatizer;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Created by Kayler on 05/05/2016.
 */
public abstract class SQFPrivatization<E extends PsiElement, T extends SQFPrivatizer> {
	private final E privateElement;
	private final T privatizer;
	private final SQFScope declarationScope;

	/**
	 A private declaration is something that can be declared private inside a given scope. Example is a variable

	 @param privateElement the element that is being declared private
	 @param privatizer the privatizer linked to this privatization
	 */
	private SQFPrivatization(@NotNull E privateElement, @Nullable T privatizer) {
		this.privateElement = privateElement;
		this.privatizer = privatizer;
		declarationScope = privatizer == null ? SQFPsiUtil.getContainingScope(this.privateElement) : SQFPsiUtil.getContainingScope(privatizer.getPrivatizerElement());
	}

	/**
	 Get the element that is being declared private (usually SQFVariable)
	 */
	@NotNull
	public E getPrivateElement() {
		return privateElement;
	}

	/**
	 Get the element that is actually declaring the privatization. Can be null if the privatization is some how inherited
	 */
	@Nullable
	public T getPrivatizer() {
		return privatizer;
	}

	/**
	 Get the scope of the declaration element. If the declaration element in this instance is null, it will return the containing scope of the private element
	 */
	@NotNull
	public SQFScope getDeclarationScope() {
		return declarationScope;
	}

	/**
	 Gets a privatization instance for the given variable and given privatizer

	 @param variable variable that is being declared private
	 @param privatizer privatizer that makes the variable private (all grammar rules that make a variable able to be turned private must implement SQFPrivatizer)
	 @return new instance
	 @throws IllegalArgumentException when the privatizer couldn't be matched to a SQFPrivatization class
	 */
	public static SQFPrivatization getPrivatization(@NotNull SQFVariable variable, @NotNull SQFPrivatizer privatizer) {
		if (privatizer instanceof SQFPrivateDecl) {
			return new SQFPrivateDeclClassic(variable, (SQFPrivateDecl) privatizer);
		}
		if (privatizer instanceof SQFParamsStatement) {
			return new SQFPrivateDeclParams(variable, (SQFParamsStatement) privatizer);
		}
		if (privatizer instanceof SQFPrivateAssignmentPrivatizer) {
			return new SQFPrivateAssignment(variable, (SQFPrivateAssignmentPrivatizer) privatizer);
		}
		throw new IllegalArgumentException("No known implementation class for privatizer element=" + privatizer);
	}

	public static class SQFVarInheritedPrivatization extends SQFPrivatization<SQFVariable, SQFScope> {

		private final SQFScope declarationScope;

		/**
		 A variable that is private because it is naturally private to its scope or is always private (magic variables only)

		 @param privateElement the element that is being declared private
		 @param declarationScope the scope in which it is declared private in
		 */
		public SQFVarInheritedPrivatization(@NotNull SQFVariable privateElement, @NotNull SQFScope declarationScope) {
			super(privateElement, null);
			this.declarationScope = declarationScope;
		}

		@Nullable
		@Override
		public SQFScope getPrivatizer() {
			return this.declarationScope;
		}
	}

	public static class SQFPrivateDeclClassic extends SQFPrivatization<SQFVariable, SQFPrivateDecl> {

		/**
		 A classic private declaration between a variable and private ["_var"];

		 @param privateVariable the element that is being declared private
		 @param declarationElement the element that actually specifies the privatization
		 */
		public SQFPrivateDeclClassic(@NotNull SQFVariable privateVariable, @NotNull SQFPrivateDecl declarationElement) {
			super(privateVariable, declarationElement);
		}
	}


	public static class SQFPrivateDeclParams extends SQFPrivatization<SQFVariable, SQFParamsStatement> {

		/**
		 A private declaration between a variable and params["_var"];

		 @param privateVariable the element that is being declared private
		 @param declarationElement the params statement that makes the variable private
		 */
		public SQFPrivateDeclParams(@NotNull SQFVariable privateVariable, @NotNull SQFParamsStatement declarationElement) {
			super(privateVariable, declarationElement);
		}
	}

	public static class SQFPrivateAssignment extends SQFPrivatization<SQFVariable, SQFPrivateAssignmentPrivatizer> {

		/**
		 Used for something like private _example =1;

		 @param privateVariable the element that is being declared private
		 @param declarationElement the assigment that makes the variable private (private _t = 1)
		 */
		public SQFPrivateAssignment(@NotNull SQFVariable privateVariable, @NotNull SQFPrivateAssignmentPrivatizer declarationElement) {
			super(privateVariable, declarationElement);
		}

	}

}
