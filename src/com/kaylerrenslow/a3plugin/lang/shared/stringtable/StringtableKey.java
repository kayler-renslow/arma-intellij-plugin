package com.kaylerrenslow.a3plugin.lang.shared.stringtable;

import com.intellij.psi.xml.XmlTag;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Container;
import com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom.Key;

/**
 * @author Kayler
 * Wrapper class for Key xml elements inside stringtable.xml
 * Created on 04/09/2016.
 */
public class StringtableKey {
	private final Key key;
	private final Container container;

	public StringtableKey(Key key, Container container) {
		this.key = key;
		this.container = container;
	}

	public String getKeyName(){
		return key.getID().getXmlAttribute().getValue();
	}

	public XmlTag getXmlTag(){
		return key.getXmlTag();
	}

	public String getContainerName(){
		return container.getName().getXmlAttribute().getValue();
	}

	/** Gets the keyname but replaces str_ with $STR_
	 * @return key name
	 */
	public String getDollarKeyName() {
		String find= "str_";
		String keyName = getKeyName().replaceFirst(find,"\\$STR_");
		return keyName;
	}
}
