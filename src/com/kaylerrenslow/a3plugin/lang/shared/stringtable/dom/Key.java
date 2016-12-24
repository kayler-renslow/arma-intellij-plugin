package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameStrategy;

/**
 * @author Kayler
 * @since 04/08/2016
 */
@NameStrategy(value = StringtableXmlNameStrategy.class)
public interface Key extends DomElement {

	@Attribute("ID")
	GenericAttributeValue<String> getID();
}
