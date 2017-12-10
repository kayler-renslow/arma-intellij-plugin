package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.*;
import com.kaylerrenslow.armaplugin.lang.sqf.psi.SQFString;

/**
 * @author Kayler
 * @since 12/09/2017
 */
@NameStrategy(value = StringTableXmlNameStrategy.class)
public interface Key extends DomElement {

	@Attribute("ID")
	@Convert(KeyConverter.class)
	GenericAttributeValue<SQFString> getID();
}

