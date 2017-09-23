package com.kaylerrenslow.armaplugin;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The way of connecting to <a href="http://store.steampowered.com/app/233800/">Arma 3 tools</a> installation
 * and using it's utilities
 *
 * @author Kayler
 * @since 09/23/2016.
 */
public class ArmaTools {

	private static final String BANK_REV = "\\BankRev\\BankRev.exe";
	private static final String CFG_CONVERT = "\\CfgConvert\\CfgConvert.exe";

	/**
	 * Test if the given File is a valid path to Arma 3 Tools.
	 * In order to be valid, the path must be something like 'STEAM_INSTALLATION_ROOT\steamapps\common\Arma 3 Tools\'
	 */
	public static boolean isValidA3ToolsDirectory(@NotNull File file) {
		if (!file.exists()) {
			return false;
		}
		return new File(file.getPath() + BANK_REV).exists();
	}

	/**
	 * Runs text in the command line. This method will freeze the current Thread for up to timeout milliseconds.
	 *
	 * @param commandLineText text to run
	 * @param timeout         how many milliseconds the operation is allowed to run
	 * @return true if the operation succeeded, false if it didn't
	 */
	private static boolean execCommandLineOperation(@NotNull String commandLineText, long timeout) {
		ProcessBuilder pb = new ProcessBuilder(commandLineText);
		Process p;
		try {
			p = pb.start();
		} catch (IOException e) {
			return false;
		}
		try {
			if (!p.waitFor(timeout, TimeUnit.MILLISECONDS)) {
				p.destroyForcibly();
				return false;
			}
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/**
	 * Utilizes "BankRev.exe", which extracts PBO's. It should be noted that this will extract the entire PBO, which could
	 * take a lot of disk memory and time.<br>
	 * This method will freeze the current Thread for up to timeout milliseconds.
	 *
	 * @param arma3ToolsDirectory the directory of Arma 3 Tools installation
	 * @param pboToExtract        the .pbo file to extract
	 * @param saveToDirectory     the directory to save the extracted contents to
	 * @param timeout             how many milliseconds the operation is allowed to take before it is suspended.
	 * @return true if the operation succeeded. Returns false if the operation failed and for an unknown reason (something bad happened with the conversion).
	 * @throws IOException when any of the given file parameters are invalid
	 */
	public static boolean extractPBO(@NotNull File arma3ToolsDirectory, @NotNull File pboToExtract, @NotNull File saveToDirectory, long timeout) throws IOException {
		checkA3ToolsDir(arma3ToolsDirectory);

		if (!pboToExtract.exists()) {
			throw new FileNotFoundException("Arma Tools: The pbo file doesn't exist. File=" + pboToExtract.getPath());
		}

		String commandLine = String.format(
				"\"%s\\\\%s\" -f \"%s\" \"%s\"",
				arma3ToolsDirectory.getPath(),
				BANK_REV,
				saveToDirectory.getPath(),
				pboToExtract.getPath()
		);
		return execCommandLineOperation(commandLine, timeout);
	}

	/**
	 * Utilizes "CfgConvert.exe", which converts binarized configs to plain text versions.<br>
	 * This method will freeze the current Thread for up to timeout milliseconds.
	 *
	 * @param arma3ToolsDirectory the directory of Arma 3 Tools installation
	 * @param binarizedCfgFile    the binarized config
	 * @param destFile            where to save the plain text converted config to
	 * @param timeout             how many milliseconds the operation is allowed to take before it is suspended.
	 * @return true if the operation succeeded. Returns false if the operation failed and for an unknown reason (something bad happened with the conversion).
	 * @throws IOException when any of the given file parameters are invalid
	 */
	public static boolean convertBinConfigToText(@NotNull File arma3ToolsDirectory, @NotNull File binarizedCfgFile, @NotNull File destFile, long timeout) throws IOException {
		checkA3ToolsDir(arma3ToolsDirectory);
		if (!binarizedCfgFile.exists()) {
			throw new FileNotFoundException("Arma Tools: The binary cfg file doesn't exist. File=" + binarizedCfgFile.getPath());
		}
		String commandLine = String.format(
				"\"%s\\\\%s\" -bin -dst \"%s\" \"%s\"",
				arma3ToolsDirectory.getPath(),
				CFG_CONVERT,
				binarizedCfgFile.getPath(),
				destFile.getPath()
		);
		return execCommandLineOperation(commandLine, timeout);
	}

	private static void checkA3ToolsDir(@NotNull File arma3ToolsDirectory) throws FileNotFoundException {
		if (!arma3ToolsDirectory.exists()) {
			throw new FileNotFoundException("Arma Tools: Arma 3 Tools Directory doesn't exist. Dir=" +
					arma3ToolsDirectory.getPath());
		}
		if (!isValidA3ToolsDirectory(arma3ToolsDirectory)) {
			throw new FileNotFoundException("Arma Tools: Path to Arma 3 Tools directory is incorrect. Dir=" +
					arma3ToolsDirectory.getPath());
		}
	}

}