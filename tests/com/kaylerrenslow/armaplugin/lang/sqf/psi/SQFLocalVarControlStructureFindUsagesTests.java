package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kayler
 * @since 11/27/2017
 */
public class SQFLocalVarControlStructureFindUsagesTests extends LightCodeInsightFixtureTestCase {

	/**
	 * This test is for checking implicit made private variables in if then control structure
	 */
	public void testControlStructuresImplicitPrivate_ifthen() throws Exception {
		System.out.println("TODO: test in game if the then code block shares scope with else block");
		System.out.println("TODO: test in game if each switch case and default block shares scope with each other");

		VariableScopeHelper fileScoped = new VariableScopeHelper("FILE");
		VariableScopeHelper ifThenScoped = new VariableScopeHelper("IF");

		fileScoped.addExpectedCount("_sharedVar", 2);
		fileScoped.addExpectedCount("_ifVar", 2);

		ifThenScoped.addExpectedCount("_ifVar", 2);
		ifThenScoped.addExpectedCount("_onlyInIfStatement", 1);
		ifThenScoped.addExpectedCount("_sharedVar", 1);

		runTest(
				"localVarFindUsagesTests.controlStructuresImplicitPrivate.ifthen.sqf",
				fileScoped,
				ifThenScoped
		);
	}

	private void runTest(@NotNull String fileName, @NotNull VariableScopeHelper fileScopeHelper,
						 @NotNull VariableScopeHelper... otherHelpers) {
		SQFFile file = (SQFFile) myFixture.configureByFile(getFilePath(fileName));

		List<SQFVariable> allVariables = new ArrayList<>();

		VariableScopeHelper[] allScopes = new VariableScopeHelper[1 + otherHelpers.length];
		allScopes[0] = fileScopeHelper;
		System.arraycopy(otherHelpers, 0, allScopes, 1, otherHelpers.length);

		PsiUtil.traverseBreadthFirstSearch(file.getNode(), astNode -> {
			PsiElement nodeAsElement = astNode.getPsi();
			if (nodeAsElement instanceof SQFVariable) {
				SQFVariable var = (SQFVariable) nodeAsElement;
				allVariables.add(var);
			}
			return false;
		});

		Pattern p = Pattern.compile("//SCOPE_HELPER:([a-zA-Z0-9_]+):(BEGIN|END)");
		Matcher m = p.matcher(file.getText());
		while (m.find()) {
			boolean begin = m.group(2).equals("BEGIN");
			for (VariableScopeHelper helper : allScopes) {
				if (helper.name.equals(m.group(1))) {
					TextRange textRange = helper.textScopeHelperRange;
					if (begin) {
						if (textRange == null) {
							helper.textScopeHelperRange = TextRange.from(m.start(), m.end());
						} else {
							throw new IllegalStateException("duplicate scope_helper begin for " + helper.name);
						}
					} else {
						if (textRange == null) {
							throw new IllegalStateException("no scope_helper begin for " + helper.name);
						} else {
							helper.textScopeHelperRange = TextRange.from(textRange.getStartOffset(), textRange.getLength());
						}
					}
				}
			}
		}

		for (VariableScopeHelper helper : allScopes) {
			if (helper != fileScopeHelper && helper.textScopeHelperRange == null) {
				throw new IllegalStateException("scope helper didn't have a range:" + helper.name);
			}
		}

		for (SQFVariable var : allVariables) {
			Collection<UsageInfo> usages = myFixture.findUsages(var);
			SQFVariableName varName = var.getVarNameObj();
			boolean matched = false;
			for (VariableScopeHelper helper : allScopes) {
				if (helper == fileScopeHelper) {
					continue;
				}
				if (helper.textScopeHelperRange.contains(var.getTextRange())) {
					helper.actualUsageCountMap.put(varName, usages.size());
					matched = true;
					break;
				}
			}
			if (!matched) {
				fileScopeHelper.actualUsageCountMap.put(varName, usages.size());
			}
		}

		for (VariableScopeHelper helper : allScopes) {
			for (Map.Entry<SQFVariableName, Integer> entry : helper.expectedUsageCountMap.entrySet()) {
				assertEquals("Variable usage count for " + entry.getKey(),
						entry.getValue(),
						helper.actualUsageCountMap.get(entry.getKey())
				);
			}
		}
	}

	private String getFilePath(String fileName) {
		return "tests/com/kaylerrenslow/armaplugin/lang/sqf/psi/testFiles/" + fileName;
	}

	private class VariableScopeHelper {
		final Map<SQFVariableName, Integer> actualUsageCountMap = new HashMap<>();
		final Map<SQFVariableName, Integer> expectedUsageCountMap = new HashMap<>();
		final String name;
		TextRange textScopeHelperRange;

		public VariableScopeHelper(@NotNull String name) {
			this.name = name;
		}

		public void addExpectedCount(@NotNull String varName, int count) {
			expectedUsageCountMap.put(new SQFVariableName(varName), count);
		}

		@Override
		public String toString() {
			return "VariableScopeHelper{" +
					"actualUsageCountMap=" + actualUsageCountMap +
					", expectedUsageCountMap=" + expectedUsageCountMap +
					", name='" + name + '\'' +
					", textScopeHelperRange=" + textScopeHelperRange +
					'}';
		}
	}
}
