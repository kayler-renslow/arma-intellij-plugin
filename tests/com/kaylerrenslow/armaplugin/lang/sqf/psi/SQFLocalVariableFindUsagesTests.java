package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.kaylerrenslow.armaDialogCreator.util.Reference;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName.nameEquals;

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
				if (nameEquals(var.getVarName(), "_var")) {
					_varElements.add(var);
				} else if (nameEquals(var.getVarName(), "_var2")) {
					_var2Elements.add(var);
				}
			}
			return false;
		});

		{ //_var assertions
			for (SQFVariable _varElement : _varElements) {
				int numValidUsages = 0;
				for (UsageInfo info : myFixture.findUsages(_varElement)) {
					if (nameEquals(info.getElement().getText(), "_var")) {
						numValidUsages++;
					}
				}
				assertEquals(6, numValidUsages);
			}
		}

		{ //_var2 assertions
			assertEquals(1, _var2Elements.size());
			assertEquals(1, myFixture.findUsages(_var2Elements.get(0)).size());
		}
	}

	/**
	 * This test is for checking implicit made private variables
	 */
	public void testControlStructuresImplicitPrivate() throws Exception {
		System.out.println("TODO: test in game if the then code block shares scope with else block");
		System.out.println("TODO: test in game if each switch case and default block shares scope with each other");

		SQFFile file = (SQFFile) myFixture.configureByFile(getFilePath("localVarFindUsagesTests.controlStructuresImplicitPrivate.sqf"));

		VariableSectionHelper notInSection = new VariableSectionHelper("notInSection");
		List<SQFVariable> allVariables = new ArrayList<>();
		Reference<VariableSectionHelper> categoryListRef = new Reference<>(notInSection);

		VariableSectionHelper _ifVarSection = new VariableSectionHelper("_ifVarSection");
		VariableSectionHelper _forVarSection = new VariableSectionHelper("_forVarSection");
		VariableSectionHelper _forVarInStringSection = new VariableSectionHelper("_forVarInStringSection");
		VariableSectionHelper _whileVarSection = new VariableSectionHelper("_whileVarSection");
		VariableSectionHelper _switchVarSection = new VariableSectionHelper("_switchVarSection");

		final VariableSectionHelper[] sections = {
				_ifVarSection,
				_forVarSection,
				_forVarInStringSection,
				_whileVarSection,
				_switchVarSection
		};

		PsiUtil.traverseDepthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof PsiComment) {
				switch (nodeAsElement.getText()) {
					case "//FIND_USAGE_SECTION:START IF": {
						categoryListRef.setValue(_ifVarSection);
						break;
					}
					case "//FIND_USAGE_SECTION:START FOR": {
						categoryListRef.setValue(_forVarSection);
						break;
					}
					case "//FIND_USAGE_SECTION:START SECOND FOR": {
						categoryListRef.setValue(_forVarInStringSection);
						break;
					}
					case "//FIND_USAGE_SECTION:START WHILE": {
						categoryListRef.setValue(_whileVarSection);
						break;
					}
					case "//FIND_USAGE_SECTION:START SWITCH": {
						categoryListRef.setValue(_switchVarSection);
						break;
					}

					case "//FIND_USAGE_SECTION:END SWITCH"://fall
					case "//FIND_USAGE_SECTION:END WHILE"://fall
					case "//FIND_USAGE_SECTION:END SECOND FOR": //fa;;
					case "//FIND_USAGE_SECTION:END FOR": //fall
					case "//FIND_USAGE_SECTION:END IF": {
						categoryListRef.setValue(notInSection);
						break;
					}
				}
			}
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;
				categoryListRef.getValue().varElements.add(var);
				allVariables.add(var);
			}
			return false;
		});

		for (SQFVariable var : allVariables) {
			Collection<UsageInfo> usages = myFixture.findUsages(var);
			String varName = var.getVarName();
			boolean matched = false;
			for (VariableSectionHelper helper : sections) {
				if (helper.varElements.contains(var)) {
					helper.usageCountMap.compute(
							varName.toLowerCase(), //put variable name in lower case to ensure case insensitivity
							(s, integer) -> integer == null ? 1 : integer + 1
					);
					matched = true;
				}
			}
			assertEquals("Variable '" + varName + "' should be in a section", true, matched);
		}

		//all variable names that should have usage count (case should not matter)
		String[] allVariableNamesExpected = {
				"_sharedVar",
				"ifVar",
				"_onlyInIfStatement",
				"_forVar",
				"_onlyInForStatement",
				"_forVarInString",
				"_whileVar",
				"_switchVar"
		};
		for (VariableSectionHelper vhs : sections) {
			boolean found = false;
			if(vhs.varElements.size() != allVariableNamesExpected.length){
				throw new IllegalStateException("section '"+vhs.name+"' has too few/many variable elements. All variables=" + vhs.varElements);
			}

			if(vhs.usageCountMap.size() != allVariableNamesExpected.length){
				throw new IllegalStateException("section '"+vhs.name+"' has too few/many usage variables. Section map=" + vhs.usageCountMap);
			}
			
			for (String varExpected : allVariableNamesExpected) {
				for (String key : vhs.usageCountMap.keySet()) {
					if (nameEquals(varExpected, key)) {
						found = true;
						break;
					}
				}
				if (!found) {
					throw new Exception("Variable '" + varExpected + "' wasn't placed in any section map");
				}
			}
		}

		assertEquals(2, notInSection.usageCountMap.get("_sharedVar".toLowerCase()).intValue());
		assertEquals(2, notInSection.usageCountMap.get("_ifVar".toLowerCase()).intValue());
		assertEquals(2, notInSection.usageCountMap.get("_forVar".toLowerCase()).intValue());
		assertEquals(1, notInSection.usageCountMap.get("_whileVar".toLowerCase()).intValue());
		assertEquals(1, notInSection.usageCountMap.get("_switchVar".toLowerCase()).intValue());

		//todo do other sections

		throw new RuntimeException("todo finish this test");
	}

	private String getFilePath(String fileName) {
		return "tests/com/kaylerrenslow/armaplugin/lang/sqf/psi/testFiles/" + fileName;
	}

	private class VariableSectionHelper {
		final Map<String, Integer> usageCountMap = new HashMap<>();
		final List<SQFVariable> varElements = new ArrayList<>();
		final String name;

		public VariableSectionHelper(@NotNull String name) {
			this.name = name;
		}
	}
}
