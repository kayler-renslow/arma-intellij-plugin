package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.kaylerrenslow.armaplugin.lang.PsiUtil;

import java.util.ArrayList;
import java.util.List;

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

	private String getFilePath(String fileName) {
		return "tests/com/kaylerrenslow/armaplugin/lang/sqf/psi/testFiles/" + fileName;
	}

}
