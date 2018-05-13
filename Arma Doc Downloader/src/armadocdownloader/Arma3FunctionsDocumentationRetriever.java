package armadocdownloader;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public class Arma3FunctionsDocumentationRetriever extends WikiDocumentationRetriever {

	private final File functionsSaveFolder = new File("arma3-functions");
	private final File functionsListHTMLFile = new File("arma3-functions-list.html");
	public static final PsiElementLinkType linkType = new PsiElementLinkType(Arma3DocumentationDownloader.PSI_ELE_TYPE_FUNCTION);

	private static final Object SOURCE_FUNCTION_LIST = new Object();
	private final List<FunctionSource> toFormat = new ArrayList<>();

	private CountDownLatch functionsDoneDownloadingLatch;

	public void run(boolean downloadIndividualFiles) {
		functionsSaveFolder.mkdirs();

		String functionsListUrl = "https://community.bistudio.com/wiki/Category:Arma_3:_Functions";
		HTMLUtil.addURLDownloadedCallback(ctx -> {
			if (ctx.getSource() == SOURCE_FUNCTION_LIST) {
				try {
					Document d = Jsoup.parse(functionsListHTMLFile, null);
					readAllFunctionsDocument(d, downloadIndividualFiles);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (ctx.getSource() instanceof FunctionSource) {
				toFormat.add((FunctionSource) ctx.getSource());
			}
			return null;
		});
		HTMLUtil.downloadHTMLToDisk(SOURCE_FUNCTION_LIST, Arma3DocumentationDownloader.BASE_WIKI_URL,
				functionsListUrl, 200,
				functionsListHTMLFile.getName(), null, null
		);
	}

	private String getUrl(@NotNull String commandName) {
		return Arma3DocumentationDownloader.WIKI_URL_PREFIX + commandName;
	}

	private void readAllFunctionsDocument(@NotNull Document d, boolean downloadIndividualFile) {
		PrintWriter functionsPw = null;
		try {
			functionsPw = new PrintWriter(new File("functions.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Elements cmdGroups = d.select(".mw-category-group");
		List<Element> liElements = new ArrayList<>();
		for (Element cmdGroupEle : cmdGroups) {
			liElements.addAll(cmdGroupEle.select("ul > li"));
		}
		functionsDoneDownloadingLatch = new CountDownLatch(liElements.size());

		Iterator<Element> liElementsiter = liElements.iterator();
		Element anchorElement;
		String url;
		String functionName;
		int id = 0;

		int currentCount = 0;
		int totalCount = liElements.size();
		for (Element liElement : liElements) {
			currentCount++;
			if (liElement.id().length() == 0) {
				anchorElement = liElement.child(0);
				url = anchorElement.attr("href");
				functionName = anchorElement.attr("title").replaceAll("\\s", "_").trim();

				if (downloadIndividualFile) {
					HTMLUtil.downloadHTMLToDisk(
							new FunctionSource(functionName),
							Arma3DocumentationDownloader.BASE_WIKI_URL,
							getUrl(functionName), 200, getHTMLFile(functionName).getName(),
							functionsSaveFolder.getAbsolutePath(),
							functionsDoneDownloadingLatch
					);
				} else {
					toFormat.add(new FunctionSource(functionName));
				}

				functionsPw.println(functionName);
				linkType.linkNames.add(functionName);
				System.out.println(currentCount + "/" + totalCount + " " + functionName);

				id++;
			}
		}

		System.out.println(">>>>>>>Function read complete.\n\n");
		functionsPw.flush();
		functionsPw.close();

		if (downloadIndividualFile) {
			try {
				functionsDoneDownloadingLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		allDoneDownloading();
	}

	@Override
	public void beginFormatting() {
		for (FunctionSource functionSource : toFormat) {
			ArmaDocumentationAsyncFormattingOperations.formatAndSaveAsync(
					getHTMLFile(functionSource.functionName),
					getResultDocFile(functionSource.functionName),
					linkType
			);
		}
		ArmaDocumentationAsyncFormattingOperations.noMoreFunctionsToFormat(this);
	}

	@Override
	@NotNull
	public File getUnformattedDocSaveFolder() {
		return functionsSaveFolder;
	}

	@NotNull
	private File getHTMLFile(@NotNull String functionName) {
		return new File(functionsSaveFolder.getAbsolutePath() + "/" + functionName + ".html");
	}

	@NotNull
	private File getResultDocFile(@NotNull String functionName) {
		return new File(Arma3DocumentationDownloader.functionDocPluginSaveFolder.getAbsolutePath() + "/" + functionName);
	}

	private static class FunctionSource {
		private final String functionName;

		public FunctionSource(@NotNull String functionName) {
			this.functionName = functionName;
		}

		@Override
		public String toString() {
			return functionName;
		}
	}
}
