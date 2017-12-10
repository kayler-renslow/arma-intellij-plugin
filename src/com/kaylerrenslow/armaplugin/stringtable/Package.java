package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.NameStrategy;

import java.util.List;

/**
 * @author Kayler
 * @since 12/09/2017
 */
@NameStrategy(value = StringTableXmlNameStrategy.class)
public interface Package extends DomElement {
	List<Container> getContainers();

	List<Key> getKeys(); //containers aren't required
}

