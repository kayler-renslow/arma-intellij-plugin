package doc;

import utils.GeneralUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Scanner;

public class Arma3Documentation {

	public static final String PSI_ELE_TYPE_FUNCTION = "bis-function";
	public static final String PSI_ELE_TYPE_COMMAND = "command";

	private final File commandsSaveLoc = Arma3CommandsHTMLParser.arma3CommandsSaveLocFile;
	private final File functionsSaveLoc = Arma3FunctionsHTMLParser.functionsSaveFolder;
	private final String pluginSrcDirPath = "../raw_doc/";
	private final String commandDocSaveLocPath = pluginSrcDirPath + "commands-doc";
	private final File commandDocSaveLoc = new File(commandDocSaveLocPath);
	private final String functionDocSaveLocPath = pluginSrcDirPath + "bis-functions-doc";

	private final File functionDocSaveLoc = new File(functionDocSaveLocPath);
	private final String lookup = "/lookup.list";
	private final File commandsDocListSaveLoc = new File(commandDocSaveLocPath + lookup);

	private final File functionsDocListSaveLoc = new File(functionDocSaveLocPath + lookup);

	public static void main(String[] args) throws Exception {
		new Arma3Documentation().run();
	}

	private void run() throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("Re-download Command Files from the wiki [y/n]?");
		boolean downloadCommandFiles = scan.nextLine().trim().equalsIgnoreCase("y");
		System.out.println("Re-download Function Files from the wiki [y/n]?");
		boolean downloadFunctionFiles = scan.nextLine().trim().equalsIgnoreCase("y");

		if (downloadCommandFiles || downloadFunctionFiles) {
			System.out.println("\nThe Arma 3 wiki documentation downloader is intended to be run once every 5 blue moons.\nPlease be considerate of Bohemia's servers.\nPress enter to continue...");
			scan.nextLine();
		}

		Arma3CommandsHTMLParser cd = new Arma3CommandsHTMLParser(downloadCommandFiles);
		Arma3FunctionsHTMLParser fd = new Arma3FunctionsHTMLParser(downloadFunctionFiles);

		cd.run();
		fd.run();

		PsiElementLinkType[] types = {cd.linkType, fd.linkType};

		System.out.println("PERMANENTLY DELETE DIRECTORY containing old command documentation located at '" + commandDocSaveLoc.getAbsolutePath() + "' [y/n]?");
		String deleteOldCommands = scan.nextLine().trim();
		if (deleteOldCommands.equalsIgnoreCase("y")) {
			System.out.println("Are you sure [y/n]?");
			String sure = scan.nextLine().trim();
			if (sure.equalsIgnoreCase("y")) {
				deleteDirectory(commandDocSaveLoc);
				System.out.println("Deleted directory " + commandDocSaveLoc.getAbsolutePath());
			}
		}
		System.out.println();
		System.out.println("PERMANENTLY DELETE DIRECTORY containing old function documentation located at '" + functionDocSaveLoc.getAbsolutePath() + "' [y/n]?");
		String deleteOldFunctions = scan.nextLine().trim();

		if (deleteOldFunctions.equalsIgnoreCase("y")) {
			System.out.println("Are you sure [y/n]?");
			String sure = scan.nextLine().trim();
			if (sure.equalsIgnoreCase("y")) {
				deleteDirectory(functionDocSaveLoc);
				System.out.println("Deleted directory " + functionDocSaveLoc.getAbsolutePath());
			}
		}
		try {
			commandDocSaveLoc.mkdirs();
			functionDocSaveLoc.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		GeneralUtils.searchFiles(functionsSaveLoc, new DocumentationSaver(functionDocSaveLoc, PSI_ELE_TYPE_FUNCTION, types));
		GeneralUtils.searchFiles(commandsSaveLoc, new DocumentationSaver(commandDocSaveLoc, PSI_ELE_TYPE_COMMAND, types));

		saveLookupLists(types);


		System.out.println("\n\nDONE");
		scan.close();

	}

	private void saveLookupLists(PsiElementLinkType[] types) throws FileNotFoundException {
		System.out.println("\n\nSaving lookup lists");

		PrintWriter pwCommands = new PrintWriter(commandsDocListSaveLoc);
		PrintWriter pwFunctions = new PrintWriter(functionsDocListSaveLoc);
		Hashtable<String, String> ignoredCommands = Arma3CommandsHTMLParser.ignoredCommands;
		for (PsiElementLinkType type : types) {
			if (type.type.equals(PSI_ELE_TYPE_COMMAND)) {
				for (String command : type.linkNames) {
					if (!ignoredCommands.contains(command)) {
						pwCommands.println(command);
					}
				}
			} else if (type.type.equals(PSI_ELE_TYPE_FUNCTION)) {
				for (String function : type.linkNames) {
					pwFunctions.println(function);
				}
			} else {
				throw new IllegalStateException("dude");
			}
		}

		pwCommands.flush();
		pwCommands.close();

		pwFunctions.flush();
		pwFunctions.close();
	}

	private static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return (directory.delete());
	}

}
