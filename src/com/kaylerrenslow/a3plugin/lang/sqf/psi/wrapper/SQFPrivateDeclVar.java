package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;

/**
 @author Kayler
 Used to store a variable name and PsiElement since it was used in a command expression that made the variable private.
 Created on 06/04/2016. */
public class SQFPrivateDeclVar {
	private final String varName;
	private final SQFPrivatizer privatizer;
	private final PsiElement myElement;

	/**
	 Create a private declaration variable (variable that is private to the function) with the given string and privatizer

	 @param var String that contains the var name (e.g. "_var"). This PsiElement is the one that will be returned in getVarElement()
	 @param privatizer the privatizer (often private["_var"];)
	 */
	public SQFPrivateDeclVar(SQFString var, SQFPrivatizer privatizer) {
		this.myElement = var;
		this.varName = var.getNonQuoteText();
		this.privatizer = privatizer;
	}

	/**
	 Create a private declaration variable (variable that is private to the function) with the given string and privatizer

	 @param var variable that contains the variable name that is private. This PsiElement is the one that will be returned in getVarElement()
	 @param privatizer privatizer (for this constructor, is usually params[])
	 */
	public SQFPrivateDeclVar(SQFVariable var, SQFPrivatizer privatizer) {
		this.myElement = var;
		this.varName = var.getVarName();
		this.privatizer = privatizer;
	}

	/** Get the PsiElement that is the part where the variable is mentioned (may be a SQFString psi element or an SQFVariable instance) */
	public PsiElement getVarElement() {
		return myElement;
	}

	/** Get the privatizer that made this variable private */
	public SQFPrivatizer getPrivatizer() {
		return privatizer;
	}

	/** Get the variable name. If the variable element is a String, the quotes will be omitted */
	public String getVarName() {
		return varName;
	}
}
