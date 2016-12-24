package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.NameStrategy;

import java.util.List;

/**
 * @author Kayler
 * @since 04/08/2016
 */
@NameStrategy(value = StringtableXmlNameStrategy.class)
public interface StringTableProject extends DomElement {
	List<Package> getPackages();

	List<Key> getKeys(); //packages aren't required
}
