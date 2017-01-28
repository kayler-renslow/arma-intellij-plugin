package doc;

import html.HTMLDownloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.FileAction;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class DocumentationSaver implements FileAction {

	private File saveLocation;

	private String docType;

	private PsiElementLinkType[] types;

	private AdditionalDocumentationParserNoManipulate addSaver;

	private static final String getDocNameCSSCode = "#firstHeading span";
	private static final String getDocumentationDescriptionCSS = "._description";

	private static final String PSI_ELEMENT = "psi_element://";

	public DocumentationSaver(File saveLocation, String docType, PsiElementLinkType[] types) {
		this(saveLocation, docType, types, null);
	}

	public DocumentationSaver(File saveLocation, String docType, PsiElementLinkType[] types, AdditionalDocumentationParserNoManipulate addSaver) {
		this.saveLocation = saveLocation;
		if (!this.saveLocation.exists()) {
			throw new IllegalStateException("folder doesn't exist");
		}
		this.docType = docType;
		this.types = types;
		this.addSaver = addSaver;
	}

	@Override
	public void doAction(File file, boolean isFolder) {
		if (isFolder) {
			return;
		}
		Document d = null;
		String description, docName;
		try {
			d = Jsoup.parse(file, null, Arma3CommandsHTMLParser.BASE_URL);
			d.setBaseUri(Arma3CommandsHTMLParser.BASE_URL);
			d.outputSettings().prettyPrint(false);
			HTMLDownloader.convertURLsToBase(d);
		} catch (IOException e) {
			e.printStackTrace();
		}

		docName = d.select(getDocNameCSSCode).get(0).text().replaceAll("\\s", "_");

		if (addSaver != null) {
			addSaver.read(d, file, docName);
		}
		Elements descriptionElements = d.select(getDocumentationDescriptionCSS);
		Elements descEleChild;
		try {
			descEleChild = descriptionElements.get(0).children();
		} catch (Exception e) {
			System.out.println("couldn't fund description element for: " + file);
			e.printStackTrace();
			return;
		}
		Iterator<Element> descEleChildIter = descEleChild.iterator();

		int childIndex = -1;
		while (descEleChildIter.hasNext()) {
			childIndex++;
			Element e = descEleChildIter.next();
			if (e.tagName().equalsIgnoreCase("h3") || e.tagName().equalsIgnoreCase("dl") || e.tagName().equalsIgnoreCase("span")) {
				if (e.tagName().equalsIgnoreCase("h3")) {
					e.html(e.text());
				}
				if (e.tagName().equalsIgnoreCase("h3") && childIndex == descEleChild.size() - 2) { //-2 because the last element isn't actually -1 for some reason
					e.remove();
				}
				continue;
			} else {
				e.remove();
			}
		}


		Elements codeEles = d.select("code, pre");
		for (Element code : codeEles) {
			for (Attribute attribute : code.attributes()) {
				code.removeAttr(attribute.getKey());
			}
		}

		Elements aEles = d.select("a");
		Iterator<Element> aElesIter = aEles.iterator();
		boolean setColorToGreen = true;
		while (aElesIter.hasNext()) {
			Element a = aElesIter.next();
			for (PsiElementLinkType type : types) {
				if (type.linkNames.contains(a.text())) {
					a.attr("href", PSI_ELEMENT + type.type + ":" + a.text());
					setColorToGreen = false;
					break;
				}
			}
			if (setColorToGreen) {
				a.attr("style", "color:008800;");
			}
			setColorToGreen = true;
		}

		cleanAttributes(d);

		description = descriptionElements.html();
		description = replaceTag(description, "code", "pre", "");
		description = description.replaceAll("/a><a", "/a> &nbsp;<a");

		//in intellij, pre behaves weird when it has a border. so nest it in a parent div that has the border
		String newPreTag = "<div style='border: 1px dashed #A9A9A9;margin:5px 0px;padding:4px;'><pre>";
		description = description.replaceAll("<pre>", newPreTag); //must come first or 2 borders will be created
		description = description.replaceAll("<pre  >", newPreTag);
		description = description.replaceAll("</pre>", "</pre></div>");

		System.out.println("saving " + docType + " doc: " + docName);
		try {
			PrintWriter pw = new PrintWriter(saveLocation.getPath() + "/" + docName);
			pw.println(description);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void cleanAttributes(Document d) {
		Elements allEles = d.select("*");
		for (Element code : allEles) {
			code.removeAttr("class");
			code.removeAttr("title");
			code.removeAttr("id");
		}
	}

	private String replaceTag(String replaceText, String tagName, String newTagName, String attributes) {
		replaceText = replaceText.replaceAll("<" + tagName + ">", "<" + newTagName + " " + attributes + " >");
		replaceText = replaceText.replaceAll("</" + tagName + ">", "</" + newTagName + ">");
		return replaceText;
	}

}
