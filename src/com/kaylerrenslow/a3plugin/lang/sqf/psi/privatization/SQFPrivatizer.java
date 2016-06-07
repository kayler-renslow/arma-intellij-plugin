package com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;

import java.util.List;

/**
 Created by Kayler on 06/03/2016.
 */
public interface SQFPrivatizer {
	/** Get the element (usually command expression) that contains the privatizer */
	PsiElement getPrivatizerElement();

	/**
	 Get all variables that are declared private
	 */
	List<SQFPrivateDeclVar> getPrivateVars();
}
