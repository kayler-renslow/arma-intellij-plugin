package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFAssignment;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;

/**
 Created by Kayler on 06/04/2016.
 */
public class SQFPrivateAssignmentPrivatizer implements SQFPrivatizer{
	private final SQFAssignment assignment;

	public SQFPrivateAssignmentPrivatizer(SQFAssignment assignment) {
		this.assignment = assignment;
	}

	@Override
	public PsiElement getPrivatizerElement() {
		return assignment;
	}
}
