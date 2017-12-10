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
	default List<StringTableKey> getAllKeys() {
		List<StringTableKey> keys = new ArrayList<>();
		for (Key k : getKeys()) {
			keys.add(new StringTableKey(k, "<No Package> / <No Container>"));
		}
		for (Package p : getPackages()) {
			String packageName = p.getPackageName();
			for (Key k : p.getKeys()) {
				keys.add(new StringTableKey(k, packageName));
			}
			for (Container c : p.getContainers()) {
				c.addAllKeys(packageName, keys);
			}
		}
		for (Container c : getContainers()) {
			c.addAllKeys("<No Package>", keys);
		}
		return keys;
	}
}

