package html;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileAction;
import utils.GeneralUtils;

import java.io.File;
import java.io.FileOutputStream;

public abstract class HTMLDownloader implements FileAction {

	private FileOutputStream fos;
	private String get;
	private Connection conn;
	private Document d = null;
	private File saveLoc;

	public HTMLDownloader(File saveLoc) {
		this.saveLoc = saveLoc;
	}

	@Override
	public void doAction(File file, boolean isFolder) {
		try {
			d = Jsoup.parse(file, null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		parsed(d, file);
	}

	public Document parse(File f) {
		Document d = null;
		try {
			d = Jsoup.parse(f, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * Called when the document has been downloaded (saved to disk) and has been parsed.
	 */
	public abstract void parsed(Document d, File file);

	/**
	 * Read the HTML files that are saved in this instance's html save location
	 */
	public void readSavedHTML() {
		GeneralUtils.searchFiles(saveLoc, this);
	}

	/**
	 * Read all the HTML files at saveLoc
	 */
	public void readHTMLFromDirectory(File saveLoc) {
		GeneralUtils.searchFiles(saveLoc, this);
	}

	public void readHTMLFromFile(File saveLoc) {
		doAction(saveLoc, false);
	}

	/**
	 * Where execution begins.
	 */
	public abstract void run();

	public void saveHTMLToDisk(String baseURL, String url, boolean followRedirects, boolean validateTLSCertificates, int skipLength, int id, String saveName) {
		saveHTMLToDisk(baseURL, url, followRedirects, validateTLSCertificates, skipLength, id, saveName, true);
	}

	public void saveHTMLToDisk(String baseURL, String url, boolean followRedirects, boolean validateTLSCertificates, int skipLength, int id, String saveName, boolean useConstructorFileAsBaseDir) {
		if (!saveLoc.exists()) {
			saveLoc.mkdir();
		}
		try {
			conn = Jsoup.connect(url);
			conn.followRedirects(followRedirects);
			conn.validateTLSCertificates(validateTLSCertificates);
			Document doc = conn.get();
			doc.outputSettings().prettyPrint(false);
			//			doc = Jsoup.parse(doc.html(), baseURL);

			//			doc.setBaseUri(baseURL);
			convertURLsToBase(doc);

			get = doc.html();

			if (get.length() <= skipLength) {
				System.out.println("skipping:" + url);
				return;
			}
			if (saveName == null) {
				saveName = "p=" + id;
			}
			File outputFile = new File((useConstructorFileAsBaseDir ? saveLoc.getAbsolutePath() + "/" : "") + saveName + ".html");
			outputFile.createNewFile();
			fos = new FileOutputStream(outputFile);
			fos.write(get.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception t) {
			//			t.printStackTrace();
			System.err.println("failed to save url: '" + url + "' to file. Error message:" + t.getMessage());
		}
	}

	public static void convertURLsToBase(Document doc) {
		Elements imgElements = doc.select("img");
		for (Element element : imgElements) {
			element.attr("src", element.absUrl("src"));
		}

		Elements hrefElements = doc.select("a");
		for (Element element : hrefElements) {
			element.attr("href", element.absUrl("href"));
		}

		Elements linkElements = doc.head().select("link");
		for (Element element : linkElements) {
			element.attr("href", element.absUrl("href"));
		}
	}

}
