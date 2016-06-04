package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFString;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;

/**
 Created by Kayler on 06/04/2016.
 */
public class SQFPrivateDeclVar {
	private final String varName;
	private final SQFPrivatizer privatizer;
	private final PsiElement myElement;

	public SQFPrivateDeclVar(SQFString var, SQFPrivatizer privatizer) {
		this.myElement = var;
		this.varName = var.getNonQuoteText();
		this.privatizer = privatizer;
	}

	public SQFPrivateDeclVar(SQFVariable var, SQFPrivatizer privatizer) {
		this.myElement = var;
		this.varName = var.getVarName();
		this.privatizer = privatizer;
	}

	public PsiElement getElement() {
		return myElement;
	}

	public SQFPrivatizer getPrivatizer() {
		return privatizer;
	}

	public String getVarName(){
		return varName;
	}
}
