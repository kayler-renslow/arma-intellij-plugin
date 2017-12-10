package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kayler
 * @since 12/09/2017
 */
@NameStrategy(value = StringTableXmlNameStrategy.class)
public interface Container extends DomElement {
	List<Key> getKeys();

	List<Container> getContainers();

	@Attribute("name")
	GenericAttributeValue<String> getName();

	/**
	 * @return the "name" attribute value as a String. If it doesn't exist, this will return "?"
	 */
	@NotNull
	default String getContainerName() {
		GenericAttributeValue<String> name = getName();
		if (name == null) {
			return "?";
		}
		if (name.getRawText() == null) {
			return "?";
		}
		return name.getRawText();
	}

	/**
	 * @param currentPath the current path to this container
	 * @param keys        add all keys from {@link #getKeys()} and from {@link #getContainers()} (recursive definition)
	 */
	default void addAllKeys(String currentPath, List<StringTableKey> keys) {
		String newPath = currentPath + "/" + getContainerName();
		for (Key k : getKeys()) {
			keys.add(new StringTableKey(k, newPath));
		}
		for (Container c : getContainers()) {
			c.addAllKeys(newPath, keys);
		}
	}
}