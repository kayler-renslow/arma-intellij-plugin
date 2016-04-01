package com.kaylerrenslow.a3plugin.lang.sqf.psi;

/**
 * @author Kayler
 * SQF grammar util class.
 *         Created on 03/19/2016.
 */
public class SQFPsiImplUtilForGrammar{

	/** Checks if the given variable name follows the general rules of function naming (requires tag, _fnc_ and then an identifier).
	 * <p>Examples: tag_fnc_function, sj_fnc_function2</p>
	 * <p>Counter Examples: tag_fn_c_function, sj_nc_function2, potatoes, _fnc_function</p>
	 * @param variable Variable to test
	 * @return true if matches, false if it doesn't
	 */
	public static boolean followsSQFFunctionNameRules(SQFVariable variable){
		return SQFPsiUtil.followsSQFFunctionNameRules(variable.getVarName());
	}

	/**Get's the assigning variable for the given SQFAssignment. Example:<br>
	 * <p>
	 *     variable = 1+1; //variable is assigning variable
	 * <p/>
	 */
	public static SQFVariable getAssigningVariable(SQFAssignment assignment){
		return assignment.getVariable();
	}

}
