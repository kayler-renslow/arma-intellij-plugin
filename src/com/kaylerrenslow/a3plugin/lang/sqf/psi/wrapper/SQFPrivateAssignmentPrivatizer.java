package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFAssignment;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;

import java.util.ArrayList;
import java.util.List;

/**
 Created by Kayler on 06/04/2016.
 */
public class SQFPrivateAssignmentPrivatizer implements SQFPrivatizer{
	private final SQFAssignment assignment;
	private final List<SQFPrivateDeclVar> privateVars = new ArrayList<>();

	public SQFPrivateAssignmentPrivatizer(SQFAssignment assignment) {
		this.assignment = assignment;
		privateVars.add(new SQFPrivateDeclVar(assignment.getAssigningVariable(), this));
	}

	@Override
	public PsiElement getPrivatizerElement() {
		return assignment;
	}

	@Override
	public List<SQFPrivateDeclVar> getPrivateVars() {
		return privateVars;
	}
}
