package doc;

import org.jsoup.nodes.Document;

import java.io.File;

public interface AdditionalDocumentationParserNoManipulate {

	void read(Document d, File f, String commandName);

}
