package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.NameStrategy;

import java.util.List;

/**
 * Created by Kayler on 04/08/2016.
 */
@NameStrategy(value = StringtableXmlNameStrategy.class)
public interface Package extends DomElement {
	List<Container> getContainers();
	List<Key> getKeys(); //containers aren't required
}
