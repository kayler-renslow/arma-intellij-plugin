package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.NameStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 12/09/2017
 */
@NameStrategy(value = StringTableXmlNameStrategy.class)
public interface StringTableProject extends DomElement {
	List<Package> getPackages();

	List<Container> getContainers();

	List<Key> getKeys();

	/**
	 * @return a list of all keys in the entire file
	 */
	@NotNull
	default List<Key> getAllKeys() {
		List<Key> keys = new ArrayList<>();
		keys.addAll(getKeys());
		for (Package p : getPackages()) {
			keys.addAll(p.getKeys());
			for (Container c : p.getContainers()) {
				c.addAllKeys(keys);
			}
		}
		for (Container c : getContainers()) {
			c.addAllKeys(keys);
		}
		return keys;
	}
}

