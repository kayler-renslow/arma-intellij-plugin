package doc;

import html.HTMLDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

public class Arma3CommandsHTMLParser extends HTMLDownloader {
	private static final String COMMAND_FILE_NAME = "arma3-commands-list";
	private static final File commandsHTMLFile = new File(COMMAND_FILE_NAME + ".html");
	private static final File ignoreCommandsFile = new File("arma3-ignore.txt");
	private static final String urlPrefix = "https://community.bistudio.com/wiki/";
	private static final String COMMANDS_URL = "https://community.bistudio.com/wiki/Category:Scripting_Commands_Arma_3";

	public static final File arma3CommandsSaveLocFile = new File("arma3-commands");
	public static final String BASE_URL = "https://community.bistudio.com";
	public static final Hashtable<String, String> ignoredCommands = new Hashtable<>();

	private final boolean downloadIndividualCommandFiles;

	public PsiElementLinkType linkType = new PsiElementLinkType(Arma3Documentation.PSI_ELE_TYPE_COMMAND);

	public Arma3CommandsHTMLParser(boolean downloadIndividualCommandFiles) {
		super(arma3CommandsSaveLocFile);
		this.downloadIndividualCommandFiles = downloadIndividualCommandFiles;
		loadIgnoreCommands();
	}

	private void loadIgnoreCommands() {
		try {
			Scanner scan = new Scanner(ignoreCommandsFile);
			while (scan.hasNextLine()) {
				String command = scan.nextLine();
				ignoredCommands.put(command, command);
			}
			scan.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void parsed(Document d, File file) {
		if (file == commandsHTMLFile) {
			readCommands(d);
		}
	}


	private void readCommands(Document d) {
		PrintWriter pwCommands = null;
		try {
			pwCommands = new PrintWriter(new File("commands.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Elements e1 = d.select(".mw-content-ltr table");

		//		Element commandsDiv = d.getElementById("mw-content-text");
		Element commandsDiv = e1.get(0);
		Elements liElements = commandsDiv.select("li");
		Iterator<Element> liElementsiter = liElements.iterator();
		Element liElement;
		Element anchorElement;
		String url;
		String commandName;
		int id = 0;
		int skipped = 0;
		int totalCount = liElements.size();
		int currentCount = 0;
		String format = "%-30s %3s %s";
		while (liElementsiter.hasNext()) {
			liElement = liElementsiter.next();
			currentCount++;
			if (liElement.id().length() == 0) {
				anchorElement = liElement.child(0);
				url = anchorElement.attr("href");
				commandName = anchorElement.attr("title").trim();
				if (commandName.contains("*") || commandName.contains("//") || commandName.contains("/") || commandName.contains(":")) {
					skipped++;
					continue;
				}

				if (commandName.equals("switch_do")) {
					commandName = "switch";
				}

				if (commandName.contains(" ")) {
					if (commandName.contains("diag")) {
						commandName = commandName.replaceAll("\\s", "_");
					} else {
						skipped++;
						continue;
					}
				}


				if (downloadIndividualCommandFiles) {
					saveHTMLToDisk(BASE_URL, getUrl(commandName), true, false, 200, id, commandName);
				}


				linkType.linkNames.add(commandName);
				if (!ignoredCommands.contains(commandName)) {
					pwCommands.println(getLexerRule(commandName));
				}
				System.out.println(currentCount + "/" + totalCount + "[" + skipped + "] " + commandName);
				id++;
			}
		}
		System.out.println(">>>>>>>Command read complete.\n\n");
		pwCommands.flush();
		pwCommands.close();
	}

	private String getUrl(String commandName) {
		return urlPrefix + commandName;
	}

	private String getLexerRule(String name) {
		return "<YYINITIAL> \"" + name + "\" { return SQFTypes.COMMAND_TOKEN; }";
	}

	@Override
	public void run() {
		saveHTMLToDisk(BASE_URL, COMMANDS_URL, false, false, 200, 0, COMMAND_FILE_NAME, false); //download the page that lists all commands

		readHTMLFromFile(commandsHTMLFile);
		//		readSavedHTML();

	}

}
