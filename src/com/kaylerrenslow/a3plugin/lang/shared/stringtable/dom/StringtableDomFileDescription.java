package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.DomFileDescription;

/**
 * Created by Kayler on 04/08/2016.
 */
public class StringtableDomFileDescription extends DomFileDescription<StringtableProject>{
	private static String PROJECT = "Project";

	public StringtableDomFileDescription() {
		super(StringtableProject.class, PROJECT, "");
	}
}
