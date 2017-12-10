package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameStrategy;

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
	 * @param keys add all keys from {@link #getKeys()} and from {@link #getContainers()} (recursive definition)
	 */
	default void addAllKeys(List<Key> keys) {
		keys.addAll(getKeys());
		for (Container c : getContainers()) {
			c.addAllKeys(keys);
		}
	}
}