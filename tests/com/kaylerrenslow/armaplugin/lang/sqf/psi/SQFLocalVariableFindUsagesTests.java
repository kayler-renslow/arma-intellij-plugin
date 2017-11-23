package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kayler
 * @since 11/23/17
 */
public class SQFLocalVariableFindUsagesTests extends LightCodeInsightFixtureTestCase {
	/**
	 * This test is for finding usages of variables that have no private
	 */
	public void testNonDeclaredPrivate() throws Exception {
		SQFFile file = (SQFFile) myFixture.configureByFile(getFilePath("localVarFindUsagesTests.nonPrivate.sqf"));
		List<SQFVariable> _varElements = new ArrayList<>();
		List<SQFVariable> _var2Elements = new ArrayList<>();
		PsiUtil.traverseDepthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;
				if (SQFVariableName.nameEquals(var.getVarName(), "_var")) {
					_varElements.add(var);
				} else if (SQFVariableName.nameEquals(var.getVarName(), "_var2")) {
					_var2Elements.add(var);
				}
			}
			return false;
		});

		{ //_var assertions
			for (SQFVariable _varElement : _varElements) {
				int numValidUsages = 0;
				for (UsageInfo info : myFixture.findUsages(_varElement)) {
					if (SQFVariableName.nameEquals(info.getElement().getText(), "_var")) {
						numValidUsages++;
					}
				}
				assertEquals(6, numValidUsages);
			}
		}

		{ //_var2 assertions
			assertEquals(1, _var2Elements.size());
			assertEquals(_var2Elements.get(0), myFixture.findUsages(_var2Elements.get(0)));
		}
	}

	/**
	 * This test is for checking implicit made private variables
	 */
	public void testControlStructuresImplicitPrivate() throws Exception {
		System.out.println("TODO: test in game if the then code block shares scope with else block");
		System.out.println("TODO: test in game if each switch case and default block shares scope with each other");

		SQFFile file = (SQFFile) myFixture.configureByFile(getFilePath("localVarFindUsagesTests.controlStructuresImplicitPrivate.sqf"));

		List<SQFVariable> notCategorized = new ArrayList<>();
		Reference<List<SQFVariable>> categoryListRef = new Reference<>(notCategorized);

		List<SQFVariable> _ifVarElements = new ArrayList<>();
		List<SQFVariable> _forVarElements = new ArrayList<>();
		List<SQFVariable> _forVarInStringElements = new ArrayList<>();
		List<SQFVariable> _whileVarElements = new ArrayList<>();
		List<SQFVariable> _switchVarElements = new ArrayList<>();

		PsiUtil.traverseDepthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof PsiComment) {
				switch (nodeAsElement.getText()) {
					case "//FIND USAGE TEST HELPER:START IF": {
						categoryListRef.setValue(_ifVarElements);
						break;
					}
					case "//FIND USAGE TEST HELPER:START FOR": {
						categoryListRef.setValue(_forVarElements);
						break;
					}
					case "//FIND USAGE TEST HELPER:START SECOND FOR": {
						categoryListRef.setValue(_forVarInStringElements);
						break;
					}
					case "//FIND USAGE TEST HELPER:START WHILE": {
						categoryListRef.setValue(_whileVarElements);
						break;
					}
					case "//FIND USAGE TEST HELPER:START SWITCH": {
						categoryListRef.setValue(_switchVarElements);
						break;
					}

					case "//FIND USAGE TEST HELPER:END SWITCH"://fall
					case "//FIND USAGE TEST HELPER:END WHILE"://fall
					case "//FIND USAGE TEST HELPER:END SECOND FOR": //fa;;
					case "//FIND USAGE TEST HELPER:END FOR": //fall
					case "//FIND USAGE TEST HELPER:END IF": {
						categoryListRef.setValue(notCategorized);
						break;
					}
				}
			}
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;
				categoryListRef.getValue().add(var);
			}
			return false;
		});
		throw new RuntimeException("todo finish this test");
	}

	private String getFilePath(String fileName) {
		return "tests/com/kaylerrenslow/armaplugin/lang/sqf/psi/testFiles/" + fileName;
	}
}
