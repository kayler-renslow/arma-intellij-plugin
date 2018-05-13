package armadocdownloader;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public class HTMLUtil {

	private static final List<Function<Context, Void>> callbacks = new ArrayList<>();
	private static final LinkedBlockingQueue<HTMLSaver> jobs = new LinkedBlockingQueue<>();

	static {
		Runnable r = () -> {
			while (true) {
				HTMLSaver saver;
				try {
					saver = jobs.take();
				} catch (InterruptedException e) {
					continue;
				}
				Connection conn = Jsoup.connect(saver.url);
				conn.followRedirects(true);
				conn.validateTLSCertificates(true);
				Document doc = null;
				try {
					doc = conn.get();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				doc.outputSettings().prettyPrint(false);
				convertURLsToBase(doc);

				try {
					boolean success = saver.save(doc);
					if (success) {
						Platform.runLater(() -> {
							for (Function<Context, Void> callback : callbacks) {
								callback.apply(new Context(saver.url, saver.source));
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setName("Web Engine Scheduler Thread 1");
		t.start();

		Thread t2 = new Thread(r);
		t2.setDaemon(true);
		t2.setName("Web Engine Scheduler Thread 2");
		t2.start();
	}

	public static void downloadHTMLToDisk(@Nullable Object source, @NotNull String baseURL, @NotNull String url, int skipLength,
										  @NotNull String saveName, @Nullable String basePath, @Nullable CountDownLatch latch) {
		jobs.add(new HTMLSaver(source, baseURL, url, skipLength, saveName, basePath, latch));
	}

	/**
	 * Adds a callback so that when a url successfully loads and saves to file, the given callback will be executed on
	 * the JavaFX thread
	 *
	 * @param callback callback to execute with the url passed in as an argument
	 */
	public static void addURLDownloadedCallback(@NotNull Function<Context, Void> callback) {
		Platform.runLater(() -> {
			//put in platform.runLater so that we don't need to synchronize on the list
			callbacks.add(callback);
		});
	}

	public static void convertURLsToBase(@NotNull Document doc) {
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

	private static class HTMLSaver {
		private final Object source;
		private final String baseURL;
		private final String url;
		private final int skipLength;
		private final String saveName;
		private final String basePath;
		private final CountDownLatch latch;

		public HTMLSaver(@Nullable Object source, @NotNull String baseURL, @NotNull String url, int skipLength, @NotNull String saveName, @Nullable String basePath, @Nullable CountDownLatch latch) {
			this.source = source;
			this.baseURL = baseURL;
			this.url = url;
			this.skipLength = skipLength;
			this.saveName = saveName;
			this.basePath = basePath;
			this.latch = latch;
		}

		public boolean save(@NotNull Document document) throws Exception {
			convertURLsToBase(document);
			String documentAsString = document.html();

			if (documentAsString.length() <= skipLength) {
				System.out.println("Skipping:" + url);
				return false;
			}

			File outputFile = new File((basePath != null ? basePath + "/" : "") + saveName);
			outputFile.createNewFile();
			System.out.println("Saving " + url + " to " + outputFile);


			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.write(documentAsString.getBytes());
			fos.flush();
			fos.close();

			if (latch != null) {
				latch.countDown();
			}

			return true;
		}

	}


	public static class Context {
		private final String url;
		private final Object source;

		public Context(@NotNull String url, @Nullable Object source) {
			this.url = url;
			this.source = source;
		}

		@Nullable
		public Object getSource() {
			return source;
		}

		@NotNull
		public String getUrl() {
			return url;
		}
	}

}
