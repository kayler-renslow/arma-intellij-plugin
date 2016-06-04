package com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper;

import com.intellij.psi.PsiElement;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.*;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatizer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 Created by Kayler on 06/04/2016.
 */
public class SQFParamsStatement implements SQFPrivatizer {
	private final SQFCommandExpression paramsCommandExpression;
	private final SQFArrayVal paramsArray;
	private final List<SQFPrivateDeclVar> privateDeclVars = new ArrayList<>();

	private SQFParamsStatement(SQFCommandExpression paramsCommandExpression, SQFArrayVal paramsArray) {
		this.paramsCommandExpression = paramsCommandExpression;
		this.paramsArray = paramsArray;
	}

	@Override
	public PsiElement getPrivatizerElement() {
		return paramsCommandExpression;
	}

	public SQFArrayVal getParamsArray() {
		return paramsArray;
	}

	public List<SQFPrivateDeclVar> getPrivateDeclVars() {
		return privateDeclVars;
	}

	/** Get a params statement instance from the given command expression. Will return null if the expression wasn't a valid params statement */
	@Nullable
	public static SQFParamsStatement parse(SQFCommandExpression expression) {
		PsiElement postfix = expression.getPostfixArgument();
		String commandName = expression.getCommand().getText();

		if (commandName.equals("params")) { //is params statement
			if (postfix instanceof SQFLiteralExpression) {
				SQFLiteralExpression literal = (SQFLiteralExpression) postfix;
				if (literal.getArrayVal() == null) {
					return null;
				}
				SQFArrayVal array = literal.getArrayVal();
				List<SQFLiteralExpression> arrayLiterals = SQFPsiUtil.getExpressionsOfType(array, SQFLiteralExpression.class);
				SQFParamsStatement paramsStatement = new SQFParamsStatement(expression, array);

				for (SQFLiteralExpression literalExpression : arrayLiterals) { //iterate over each string in private ["_var1","_var2"]
					if (literalExpression.getString() != null) {
						paramsStatement.privateDeclVars.add(new SQFPrivateDeclVar(literalExpression.getString(), paramsStatement));
					} else if (literalExpression.getArrayVal() != null) { //params[["_var1", _defaultValue], "_var2"]
						SQFArrayVal innerArray = literalExpression.getArrayVal();
						List<SQFArrayEntry> innerArrayEntries = innerArray.getArrayEntryList();
						if (innerArrayEntries.size() > 1) {
							SQFExpression firstExpression = innerArrayEntries.get(0).getExpression();
							if (firstExpression instanceof SQFLiteralExpression) {
								SQFLiteralExpression possibleString = (SQFLiteralExpression) firstExpression;
								if (possibleString.getString() != null) {
									paramsStatement.privateDeclVars.add(new SQFPrivateDeclVar(possibleString.getString(), paramsStatement));
								}
							}
						}
					}
				}

			}
		}
		return null;
	}
}
