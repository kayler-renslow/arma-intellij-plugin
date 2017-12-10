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
public interface Package extends DomElement {
	List<Container> getContainers();

	List<Key> getKeys();

	@Attribute("name")
	GenericAttributeValue<String> getName();

	/**
	 * @return the "name" attribute value as a String. If it doesn't exist, this will return "?"
	 */
	@NotNull
	default String getPackageName() {
		GenericAttributeValue<String> name = getName();
		if (name == null) {
			return "?";
		}
		if (name.getRawText() == null) {
			return "?";
		}
		return name.getRawText();
	}
}

