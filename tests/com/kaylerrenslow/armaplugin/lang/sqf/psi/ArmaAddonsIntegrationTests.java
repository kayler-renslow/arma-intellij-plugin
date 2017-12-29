package com.kaylerrenslow.armaplugin.lang.sqf.psi;

import com.intellij.openapi.module.Module;
import com.kaylerrenslow.armaDialogCreator.arma.header.HeaderFile;
import com.kaylerrenslow.armaplugin.ArmaPluginUserData;

import java.io.File;
import java.util.List;

/**
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

	public void testInclude_moduleWithAddonsFolder() {

		final String pathPrefix = pathToTestFiles + "test_files/addonTests/moduleWithAddonsFolder/";

		String[] filesToCopy = {
				"Addons/fake_extracted_pbo/config.cpp",
				"Addons/fake_extracted_pbo/fn_myFunction.sqf"
		};

		for (String file : filesToCopy) {
			myFixture.copyFileToProject(pathPrefix + file, file);
		}

		Module module = myFixture.getModule();
		List<HeaderFile> headerFiles = ArmaPluginUserData.getInstance().parseAndGetConfigHeaderFiles(module);
		System.out.println("ArmaAddonsIntegrationTests.testInclude_moduleWithAddonsFolder headerFiles=" + headerFiles);
	}

}
