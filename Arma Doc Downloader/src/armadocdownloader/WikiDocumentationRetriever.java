package armadocdownloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 09/20/2017
 */
public abstract class WikiDocumentationRetriever {
	private final ArrayList<Function<WikiDocumentationRetriever, Void>> completionCallbacks = new ArrayList<>();

	public abstract void run(boolean downloadIndividualFiles);

	public abstract void finish();

	@NotNull
	public abstract File getUnformattedDocSaveFolder();

	/**
	 * Add a callback for when the {@link #run(boolean)} method has finished
	 */
	public void addRunCompletionCallback(@NotNull Function<WikiDocumentationRetriever, Void> callback) {
		completionCallbacks.add(callback);
	}

	protected void completedRun() {
		for (Function<WikiDocumentationRetriever, Void> callback : completionCallbacks) {
			callback.apply(this);
		}
	}
}
