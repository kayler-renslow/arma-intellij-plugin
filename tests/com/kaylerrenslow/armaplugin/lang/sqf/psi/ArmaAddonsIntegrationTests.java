package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;
import com.kaylerrenslow.armaplugin.lang.header.HeaderConfigFunction;
import com.kaylerrenslow.armaplugin.lang.sqf.SQFVariableName;

import java.io.File;
import java.util.List;

/**
 * These tests are for testing Arma Addons integration.
 * These tests should fully test detecting whether or not the project/module is a Arma Mission project or a Addon project/module.
 * Also, they should test if {@link com.kaylerrenslow.armaplugin.ArmaAddonsManager} works correctly and content assist works
 * similar to if the project/module was an Arma Mission version.
 *
 * @author Kayler
 * @since 12/28/2017
 */
public class ArmaAddonsIntegrationTests extends SQFSyntaxCheckerTestHelper {
	/**
	 * The starting path to where the test_files directory is contained in.
	 * This should be where Arma IntelliJ Plugin project directory is.
	 * <p>
	 * This is retrieved by getting the working directory of the current Java process and then appending '/' to the end
	 * to make it easy for String concatenation.
	 */
	private final String pathToTestFiles = new File("").getAbsolutePath() + "/";

	public void test_moduleWithAddonsFolder() {

		final String pathPrefix = pathToTestFiles + "test_files/addonTests/moduleWithAddonsFolder/";

		String pboPathPrefix1 = "Addons/fake_extracted_pbo/";
		String pboPathPrefix2 = "Addons/fake_extracted_pbo2/";
		String[] filesToCopy = {
				pboPathPrefix1 + "config.cpp",
				pboPathPrefix1 + "fn_myFunction.sqf",
				pboPathPrefix1 + "directory/config.cpp",
				pboPathPrefix1 + "directory/headerFile.h",
				//second pbo
				pboPathPrefix2 + "config.cpp",
				pboPathPrefix2 + "fn_myFunctionNumeroDos.sqf",
				pboPathPrefix2 + "directory/config.cpp",
				pboPathPrefix2 + "directory/headerFile.h",
		};

		for (String file : filesToCopy) {
			myFixture.copyFileToProject(pathPrefix + file, file);
		}

		Module module = myFixture.getModule();
		ArmaPluginUserData userData = ArmaPluginUserData.getInstance();
		List<HeaderFile> headerFiles = userData.parseAndGetConfigHeaderFiles(module);

		assertEquals("There should be 4 config.cpp files detected and parsed.", 4, headerFiles.size());


		List<HeaderConfigFunction> allConfigFunctions = userData.getAllConfigFunctions(module);
		assertNotNull("The functions should have been retrieved successfully and not be null.", allConfigFunctions);

		final String[] expectedConfigFunctionsToBeFound = {
				"tag_fnc_MyFunction",
				"tag_fnc_MyFunctionNumeroDos"
		};
		assertEquals("Expected this many config functions. ", expectedConfigFunctionsToBeFound.length, allConfigFunctions.size());
		for (String expectFunc : expectedConfigFunctionsToBeFound) {
			boolean found = false;
			for (HeaderConfigFunction function : allConfigFunctions) {
				if (SQFVariableName.nameEquals(function.getCallableName(), expectFunc)) {
					found = true;
					break;
				}
			}
			if (!found) {
				fail("Expected " + expectFunc + " to exist.");
			}
		}

	}

}
