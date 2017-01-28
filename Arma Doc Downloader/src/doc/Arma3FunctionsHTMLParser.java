package doc;

import html.HTMLDownloader;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;

public class Arma3FunctionsHTMLParser extends HTMLDownloader {

	public static final File functionsSaveFolder = new File("arma3-functions");

	private static final String FUNCTIONS_FILE_NAME = "arma3-functions-list";
	private static final File FUNCTIONS_FILE = new File(FUNCTIONS_FILE_NAME + ".html");
	private static final String BASE_URL = "https://community.bistudio.com";
	private static final String FUNCTIONS_URL = "https://community.bistudio.com/wiki/Category:Arma_3:_Functions";
	private static final String urlPrefix = "https://community.bistudio.com/wiki/";


	private final boolean downloadIndividualFunctionFiles;
	public PsiElementLinkType linkType = new PsiElementLinkType(Arma3Documentation.PSI_ELE_TYPE_FUNCTION);

	public Arma3FunctionsHTMLParser(boolean downloadIndividualFunctionFiles) {
		super(functionsSaveFolder);
		this.downloadIndividualFunctionFiles = downloadIndividualFunctionFiles;
	}

	@Override
	public void parsed(Document d, File file) {
		PrintWriter pwCommands = null;
		try {
			pwCommands = new PrintWriter(new File("functions.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Elements e1 = d.select(".mw-content-ltr table");

		//		Element commandsDiv = d.getElementById("mw-content-text");
		Element functionsDiv = e1.get(0);
		Elements liElements = functionsDiv.select("li");
		Iterator<Element> liElementsiter = liElements.iterator();
		Element liElement;
		Element anchorElement;
		String url;
		String functionName;
		int id = 0;

		int currentCount = 0;
		int totalCount = liElements.size();
		while (liElementsiter.hasNext()) {
			liElement = liElementsiter.next();
			currentCount++;
			if (liElement.id().length() == 0) {
				anchorElement = liElement.child(0);
				url = anchorElement.attr("href");
				functionName = anchorElement.attr("title").replaceAll("\\s", "_").trim();

				if (downloadIndividualFunctionFiles) {
					saveHTMLToDisk(BASE_URL, getUrl(functionName), true, false, 200, id, functionName);
				}

				pwCommands.println(functionName);
				linkType.linkNames.add(functionName);
				System.out.println(currentCount + "/" + totalCount + " " + functionName);

				id++;
			}
		}
		System.out.println(">>>>>>>Function read complete.\n\n");
		pwCommands.flush();
		pwCommands.close();
	}

	private String getUrl(String commandName) {
		return urlPrefix + commandName;
	}

	@Override
	public void run() {
		saveHTMLToDisk(BASE_URL, FUNCTIONS_URL, false, false, 200, 0, FUNCTIONS_FILE_NAME, false);
		readHTMLFromFile(FUNCTIONS_FILE);
	}

}
