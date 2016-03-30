package com.kaylerrenslow.a3plugin.lang.sqf.psi;

/**
 * @author Kayler
 * SQF grammar util class.
 *         Created on 03/19/2016.
 */
public class SQFPsiImplUtilForGrammar{

	/**Get's the assigning variable for the given SQFAssignment. Example:<br>
	 * <p>
	 *     variable = 1+1; //variable is assigning variable
	 * <p/>
	 */
	public static SQFVariable getAssigningVariable(SQFAssignment assignment){
		return assignment.getVariable();
	}

}
