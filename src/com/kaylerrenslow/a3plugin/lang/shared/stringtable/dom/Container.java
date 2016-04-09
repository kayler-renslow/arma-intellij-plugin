package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameStrategy;

import java.util.List;

/**
 * Created by Kayler on 04/08/2016.
 */
@NameStrategy(value = StringtableXmlNameStrategy.class)
public interface Container extends DomElement {
	List<Key> getKeys();

	@Attribute("name")
	GenericAttributeValue<String> getName();
}
