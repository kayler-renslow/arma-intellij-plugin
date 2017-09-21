package armadocdownloader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PsiElementLinkType {

	public final String type;
	public final List<String> linkNames = Collections.synchronizedList(new ArrayList<>());

	public static final Collection<PsiElementLinkType> allTypes = new ArrayList<>();

	public PsiElementLinkType(@NotNull String type) {
		this.type = type;
		allTypes.add(this);
	}

}
