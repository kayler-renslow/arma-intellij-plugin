package armadocdownloader;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public class ArmaDocumentationIntelliJFormatter {
	private static final String getDocNameCSSCode = "#firstHeading span";
	private static final String getDocumentationDescriptionCSS = "._description";

	private static final LinkedBlockingQueue<Job> jobs = new LinkedBlockingQueue<>();

	private static final AtomicInteger endJobsCount = new AtomicInteger(0);

	private static final Job END_COMMANDS = new Job();
	private static final Job END_FUNCTIONS = new Job();

	static {
		Runnable r = () -> {
			while (endJobsCount.get() < 2) {
				try {
					Job take = jobs.take();
					if (take == END_COMMANDS) {
						endJobsCount.incrementAndGet();
						System.out.println("No more commands to format.");
						continue;
					}
					if (take == END_FUNCTIONS) {
						endJobsCount.incrementAndGet();
						System.out.println("No more BIS functions to format.");
						continue;
					}
					doFormatAndSave(take.srcFile, take.destFile, take.linkType);
				} catch (InterruptedException e) {
					continue;
				}
			}
			System.out.println("\n\n\nDONE");
			Platform.exit();
		};
		Thread t = new Thread(r);
		t.setName("IntelliJ Formatted Documentation Saver Thread 1");
		t.setDaemon(false);
		t.start();

		Thread t2 = new Thread(r);
		t2.setName("IntelliJ Formatted Documentation Saver Thread 2");
		t2.setDaemon(false);
		t2.start();
	}

	public static void endCommands() {
		jobs.add(END_COMMANDS);
	}

	public static void endFunctions() {
		jobs.add(END_FUNCTIONS);
	}

	public static void formatAndSaveAsync(@NotNull File srcFile, @NotNull File destFile, @NotNull PsiElementLinkType linkType) {
		jobs.add(new Job(srcFile, destFile, linkType));
	}

	private static void doFormatAndSave(@NotNull File srcFile, @NotNull File destFile, @NotNull PsiElementLinkType linkType) {
		final Document document;
		String description, docName;
		try {
			document = Jsoup.parse(srcFile, null, Arma3DocumentationDownloader.BASE_WIKI_URL);
			document.setBaseUri(Arma3DocumentationDownloader.BASE_WIKI_URL);
			document.outputSettings().prettyPrint(false);
			HTMLUtil.convertURLsToBase(document);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		docName = document.select(getDocNameCSSCode).get(0).text().replaceAll("\\s", "_");

		Elements codeEles = document.select("code, pre");
		for (Element code : codeEles) {
			for (Attribute attribute : code.attributes()) {
				code.removeAttr(attribute.getKey());
			}
		}

		//make non-function and non-command links green
		Elements aEles = document.select("a");
		for (Element aEle : aEles) {
			boolean setColorToGreen = true;
			for (PsiElementLinkType type : PsiElementLinkType.allTypes) {
				if (type.linkNames.contains(aEle.text())) {
					aEle.attr("href", "psi_element://" + type.type + ":" + aEle.text());
					setColorToGreen = false;
					break;
				}
			}
			if (setColorToGreen) {
				aEle.attr("style", "color:008800;");
			}
		}

		StringBuilder allNotesBuilder = new StringBuilder();
		{ //this needs to come before we trim down the description, otherwise it will get deleted
			/*As of September 21, 2017, the wiki's notes are formatted as follows:
			* <dl class="command_description"> <!-- This dl element can repeat multiple times-->
			*     <!-- START_NOTE-->
			*     <dd class="notedate">note's date</dd>
			*     <dt class="note">author</dd>
			*     <dd class="note">the actual note body</dd>
			*     <!-- END_NOTE-->
			* </dl>
			*
			* Between HTML comment "START_NOTE" "END_NOTE" defines a note's date, author, and body.
			* Each of those elements are repeated for every note.
			* */
			allNotesBuilder.append("<h3>Notes</h3>");
			int noteCount = 0;
			for (Element noteCluster : document.getElementsByClass("command_description")) {
				int notePart = 0;//when reached 2, that defines a whole note's date, author, and body
				StringBuilder singleNoteBuilder = new StringBuilder();
				for (Element noteClusterChild : noteCluster.children()) {
					if (noteClusterChild.hasClass("note") || noteClusterChild.hasClass("notedate")) {
						String elementHtml = fixTags(noteClusterChild.html());
						singleNoteBuilder.append("<div>");
						singleNoteBuilder.append(notePart == 0 ? "<b>Date: </b>" : notePart == 1 ? "<b>Author: </b>" : "");
						singleNoteBuilder.append(elementHtml);
						singleNoteBuilder.append("</div>");

						if (notePart < 2) {
							notePart++;
						} else {
							allNotesBuilder.append("<div style=\"border:2px solid #cacaca;padding:4px;\"");
							allNotesBuilder.append(singleNoteBuilder);
							allNotesBuilder.append("</div><br/>");
							singleNoteBuilder = new StringBuilder();
							notePart = 0;
							noteCount++;
						}
					}
				}
			}
			if (noteCount == 0) {
				allNotesBuilder.append("No notes.");
			}
		}

		Elements descriptionElements = document.select(getDocumentationDescriptionCSS);
		Elements descEleChild;
		try {
			descEleChild = descriptionElements.get(0).children();
		} catch (Exception e) {
			System.out.println("couldn't find description element for: " + srcFile);
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

		cleanAttributes(document);

		description = fixTags(descriptionElements.html());

		System.out.println("Saving " + linkType.type + " doc: " + docName);
		try {
			PrintWriter pw = new PrintWriter(destFile);
			pw.println(description);
			pw.flush();
			pw.println(allNotesBuilder.toString());
			pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void cleanAttributes(@NotNull Document d) {
		Elements allEles = d.select("*");
		for (Element code : allEles) {
			code.removeAttr("class");
			code.removeAttr("title");
			code.removeAttr("id");
		}
	}

	private static String fixTags(@NotNull String replaceText) {
		replaceText = replaceTag(replaceText, "code", "pre", "");
		replaceText = replaceText.replaceAll("/a><a", "/a> &nbsp;<a");

		//in intellij, pre behaves weird when it has a border. so nest it in a parent div that has the border
		String newPreTag = "<div style='border: 1px dashed #A9A9A9;margin:5px 0px;padding:4px;'><pre>";

		replaceText = replaceText.replaceAll("<pre[ ]*>", newPreTag); //must come first or 2 borders will be created
		replaceText = replaceText.replaceAll("</pre>", "</pre></div>");
		return replaceText;
	}

	private static String replaceTag(@NotNull String replaceText, @NotNull String tagName, @NotNull String newTagName, @NotNull String attributes) {
		replaceText = replaceText.replaceAll("<" + tagName + ">", "<" + newTagName + " " + attributes + " >");
		replaceText = replaceText.replaceAll("</" + tagName + ">", "</" + newTagName + ">");
		return replaceText;
	}


	private static class Job {
		private File srcFile;
		private File destFile;
		private PsiElementLinkType linkType;

		public Job() {
		}

		public Job(@NotNull File srcFile, @NotNull File destFile, @NotNull PsiElementLinkType linkType) {
			this.srcFile = srcFile;
			this.destFile = destFile;
			this.linkType = linkType;
		}
	}
}
