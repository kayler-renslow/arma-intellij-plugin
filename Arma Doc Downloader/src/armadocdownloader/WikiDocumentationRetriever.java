package armadocdownloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public abstract class WikiDocumentationRetriever {
	private final ArrayList<Function<WikiDocumentationRetriever, Void>> completionCallbacks = new ArrayList<>();

	public abstract void run(boolean downloadIndividualFiles);

	public abstract void beginFormatting();

	private final CountDownLatch formattingFinished = new CountDownLatch(1);

	@NotNull
	public abstract File getUnformattedDocSaveFolder();

	/**
	 * Add a callback for when all HTML has been downloaded
	 */
	public void addDownloadCompletionCallback(@NotNull Function<WikiDocumentationRetriever, Void> callback) {
		completionCallbacks.add(callback);
	}

	protected void allDoneDownloading() {
		for (Function<WikiDocumentationRetriever, Void> callback : completionCallbacks) {
			callback.apply(this);
		}
	}

	public void allDoneFormatting() {
		formattingFinished.countDown();
	}

	public void waitForFormattingToFinish() {
		try {
			formattingFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
