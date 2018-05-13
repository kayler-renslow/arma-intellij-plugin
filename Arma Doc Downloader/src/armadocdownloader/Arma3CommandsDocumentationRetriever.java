package armadocdownloader;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public class Arma3CommandsDocumentationRetriever extends WikiDocumentationRetriever {
	private final File commandsListHTMLFile = new File("arma3-commands-list.html");
	private final File commandsSaveFolder = new File("arma3-commands");
	private final Hashtable<String, String> ignoredCommands = new Hashtable<>();

	private static final Object SOURCE_COMMAND_LIST = new Object();

	public static final PsiElementLinkType linkType = new PsiElementLinkType(Arma3DocumentationDownloader.PSI_ELE_TYPE_COMMAND);

	private final List<CommandSource> toFormat = new ArrayList<>();

	private CountDownLatch commandsDoneDownloadingLatch;

	public Arma3CommandsDocumentationRetriever() {
		//load ignored commands
		try {
			File ignoreCommandsFile = new File("arma3-ignore.txt");
			if (!ignoreCommandsFile.exists() || ignoreCommandsFile.isDirectory()) {
				return;
			}
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
	public void run(boolean downloadIndividualFiles) {
		commandsSaveFolder.mkdirs();

		String commandsListUrl = "https://community.bistudio.com/wiki/Category:Scripting_Commands_Arma_3";
		HTMLUtil.addURLDownloadedCallback(ctx -> {
			if (ctx.getSource() == SOURCE_COMMAND_LIST) {
				try {
					Document d = Jsoup.parse(commandsListHTMLFile, null);
					readAllCommandsDocument(d, downloadIndividualFiles);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (ctx.getSource() instanceof CommandSource) {
				toFormat.add((CommandSource) ctx.getSource());
			}
			return null;
		});
		HTMLUtil.downloadHTMLToDisk(SOURCE_COMMAND_LIST, Arma3DocumentationDownloader.BASE_WIKI_URL,
				commandsListUrl, 200,
				commandsListHTMLFile.getName(), null, null
		);
	}

	private void readAllCommandsDocument(@NotNull Document d, boolean downloadIndividualFiles) {
		PrintWriter pwCommands = null;
		try {
			pwCommands = new PrintWriter(new File("commands.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Elements cmdGroups = d.select(".mw-category-group");
		List<Element> liElements = new ArrayList<>();
		for (Element cmdGroupEle : cmdGroups) {
			liElements.addAll(cmdGroupEle.select("ul > li"));
		}

		commandsDoneDownloadingLatch = new CountDownLatch(liElements.size());

		Element anchorElement;
		String url;
		String commandName;
		int id = 0;
		int skipped = 0;
		int totalCount = liElements.size();
		int currentCount = 0;
		String format = "%-30s %3s %s";
		for (Element liElement : liElements) {
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

				if (downloadIndividualFiles) {
					HTMLUtil.downloadHTMLToDisk(new CommandSource(commandName),
							Arma3DocumentationDownloader.BASE_WIKI_URL,
							getUrl(commandName), 200, getHTMLFile(commandName).getName(),
							commandsSaveFolder.getAbsolutePath(),
							commandsDoneDownloadingLatch
					);
				} else {
					toFormat.add(new CommandSource(commandName));
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

		if (downloadIndividualFiles) {
			for (int i = 0; i < skipped; i++) {
				//since we aren't actually downloading these, we will want to "mark" them as complete
				commandsDoneDownloadingLatch.countDown();
			}
			try {
				commandsDoneDownloadingLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		allDoneDownloading();
	}

	@Override
	public void beginFormatting() {
		for (CommandSource commandSource : toFormat) {
			ArmaDocumentationAsyncFormattingOperations.formatAndSaveAsync(
					getHTMLFile(commandSource.commandName),
					getResultDocFile(commandSource.commandName),
					linkType
			);
		}
		ArmaDocumentationAsyncFormattingOperations.noMoreCommandsToFormat(this);
	}

	private String getUrl(@NotNull String commandName) {
		return Arma3DocumentationDownloader.WIKI_URL_PREFIX + commandName;
	}

	private String getLexerRule(@NotNull String name) {
		return "<YYINITIAL> \"" + name + "\" { return SQFTypes.COMMAND_TOKEN; }";
	}

	@NotNull
	public Hashtable<String, String> getIgnoredCommands() {
		return ignoredCommands;
	}

	@Override
	@NotNull
	public File getUnformattedDocSaveFolder() {
		return commandsSaveFolder;
	}

	@NotNull
	private File getHTMLFile(@NotNull String commandName) {
		return new File(commandsSaveFolder.getAbsolutePath() + "/" + commandName + ".html");
	}

	@NotNull
	private File getResultDocFile(@NotNull String commandName) {
		return new File(Arma3DocumentationDownloader.commandDocPluginSaveFolder.getAbsolutePath() + "/" + commandName);
	}

	private static class CommandSource {
		private final String commandName;

		public CommandSource(@NotNull String commandName) {
			this.commandName = commandName;
		}

		@Override
		public String toString() {
			return commandName;
		}
	}
}
