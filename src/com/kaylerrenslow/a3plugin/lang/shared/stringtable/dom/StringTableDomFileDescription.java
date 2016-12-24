package com.kaylerrenslow.a3plugin.lang.shared.stringtable.dom;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Kayler
 * @since 04/08/2016
 */
public class StringTableDomFileDescription extends DomFileDescription<StringTableProject> {
	private static String PROJECT = "Project";

	public StringTableDomFileDescription() {
		super(StringTableProject.class, PROJECT, "");
	}
}
