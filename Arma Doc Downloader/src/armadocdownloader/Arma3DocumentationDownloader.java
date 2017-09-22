package armadocdownloader;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public class Arma3DocumentationDownloader extends Application {
	public static final String PSI_ELE_TYPE_FUNCTION = "bis-function";
	public static final String PSI_ELE_TYPE_COMMAND = "command";
	public static final String BASE_WIKI_URL = "https://community.bistudio.com";
	public static final String WIKI_URL_PREFIX = "https://community.bistudio.com/wiki/";

	private static final String pluginSrcDirPath = "../raw_doc/";
	public static final File commandDocPluginSaveFolder = new File(pluginSrcDirPath + "commands-doc");
	public static final File functionDocPluginSaveFolder = new File(pluginSrcDirPath + "bis-functions-doc");

	private final Arma3CommandsDocumentationRetriever commandsDocRetriever;
	private final Arma3FunctionsDocumentationRetriever functionsDocRetriever;

	public Arma3DocumentationDownloader() {
		commandsDocRetriever = new Arma3CommandsDocumentationRetriever();
		functionsDocRetriever = new Arma3FunctionsDocumentationRetriever();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("Re-download Command Files from the wiki [y/n]?");
		boolean downloadCommandFiles = scan.nextLine().trim().equalsIgnoreCase("y");
		System.out.println("Re-download Function Files from the wiki [y/n]?");
		boolean downloadFunctionFiles = scan.nextLine().trim().equalsIgnoreCase("y");

		if (downloadCommandFiles || downloadFunctionFiles) {
			System.out.println("\nThe Arma 3 wiki documentation downloader is intended to be run once every 5 blue moons.\nPlease be considerate of Bohemia's servers.\nPress enter to continue...");
			scan.nextLine();
		}


		if (downloadCommandFiles) {
			System.out.println("PERMANENTLY DELETE DIRECTORY containing old command documentation located at '" + commandsDocRetriever.getUnformattedDocSaveFolder().getAbsolutePath() + "' [y/n]?");
			String deleteOldCommands = scan.nextLine().trim();
			if (deleteOldCommands.equalsIgnoreCase("y")) {
				System.out.println("Are you sure [y/n]?");
				String sure = scan.nextLine().trim();
				if (sure.equalsIgnoreCase("y")) {
					deleteDirectory(commandsDocRetriever.getUnformattedDocSaveFolder());
				}
			}
		}
		if (downloadFunctionFiles) {
			System.out.println();
			System.out.println("PERMANENTLY DELETE DIRECTORY containing old function documentation located at '" + functionsDocRetriever.getUnformattedDocSaveFolder().getAbsolutePath() + "' [y/n]?");
			String deleteOldFunctions = scan.nextLine().trim();

			if (deleteOldFunctions.equalsIgnoreCase("y")) {
				System.out.println("Are you sure [y/n]?");
				String sure = scan.nextLine().trim();
				if (sure.equalsIgnoreCase("y")) {
					deleteDirectory(functionsDocRetriever.getUnformattedDocSaveFolder());
				}
			}
		}
		scan.close();

		Function<WikiDocumentationRetriever, Void> callback = new Function<WikiDocumentationRetriever, Void>() {
			boolean completedCommands = false;
			boolean completedFunctions = false;

			@Override
			public Void apply(WikiDocumentationRetriever wikiDocumentationRetriever) {
				completedCommands = completedCommands || wikiDocumentationRetriever == commandsDocRetriever;
				completedFunctions = completedFunctions || wikiDocumentationRetriever == functionsDocRetriever;
				if (completedFunctions && completedCommands) {
					//here, all of the commands and functions have been discovered, so we can run the finish methods
					//and make lookup lists
					commandsDocRetriever.finish();
					functionsDocRetriever.finish();

					try {
						saveLookupLists();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
		commandsDocRetriever.addRunCompletionCallback(callback);
		functionsDocRetriever.addRunCompletionCallback(callback);

		commandsDocRetriever.run(downloadCommandFiles);
		functionsDocRetriever.run(downloadFunctionFiles);
	}

	@Override
	public void stop() throws Exception {
	}

	public static void main(String[] args) {
		launch(args);
	}


	private void saveLookupLists() throws FileNotFoundException {
		PrintWriter pwCommands = new PrintWriter(commandDocPluginSaveFolder.getAbsolutePath() + "/lookup.list");
		PrintWriter pwFunctions = new PrintWriter(functionDocPluginSaveFolder.getAbsolutePath() + "/lookup.list");
		Hashtable<String, String> ignoredCommands = commandsDocRetriever.getIgnoredCommands();
		for (PsiElementLinkType type : PsiElementLinkType.allTypes) {
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
		System.out.println("\n\nSaved lookup lists");
	}

	private static void deleteDirectory(@NotNull File directory) {
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
		directory.delete();
		System.out.println("Deleted " + directory.getAbsolutePath());
	}
}
