package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for {@link Key} that contains additional information
 *
 * @author Kayler
 * @since 12/10/2017
 */
public class StringTableKey {
	@NotNull
	private final Key key;
	@NotNull
	private final String containerPath;

	public StringTableKey(@NotNull Key key, @Nullable String containerPath) {
		this.key = key;
		this.containerPath = containerPath == null ? "" : containerPath;
	}

	/**
	 * @return the XML element that this class wraps
	 */
	@NotNull
	public Key getKey() {
		return key;
	}

	/**
	 * @return a user-friendly display path that shows the package and container to get to the key
	 */
	@NotNull
	public String getContainerPath() {
		return containerPath;
	}

	/**
	 * @return the key's id attribute
	 */
	@NotNull
	public String getID() {
		XmlAttribute attr = key.getID().getXmlAttribute();
		return attr == null || attr.getValue() == null ? "<No ID>" : attr.getValue();
	}

	/**
	 * @return the {@link XmlTag} associated with {@link #getID()}
	 */
	@NotNull
	public XmlTag getIDXmlTag() {
		return key.getID().getXmlTag();
	}

	@Override
	public String toString() {
		return getID();
	}

	/**
	 * Gets the ID ({@link #getID()}) but replaces str_ with $STR_
	 *
	 * @return key id
	 */
	@NotNull
	public String getDollarKeyName() {
		return "$" + getID().replaceFirst("[sS][tT][rR]_", "STR_");
	}

	/**
	 * Get documentation html for the given StringTable key XmlTag (this should be one returned from {@link #getIDXmlTag()})
	 *
	 * @param element key's tag
	 * @return html showing all languages' values, or null if element was null, or null if element wasn't a key tag
	 */
	@Nullable
	public static String getKeyDoc(@Nullable XmlTag element) {
		if (element == null) {
			return null;
		}
		if (!element.getName().equalsIgnoreCase("key")) {
			return null;
		}

		final String format = "<div><b>%s</b> : <pre>%s</pre></div>";
		StringBuilder doc = new StringBuilder();
		for (XmlTag childTag : element.getSubTags()) {
			doc.append(String.format(format, childTag.getName(), childTag.getText()));
		}
		return doc.toString();
	}
}
