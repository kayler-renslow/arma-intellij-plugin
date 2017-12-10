package com.kaylerrenslow.armaplugin.stringtable;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author Kayler
 * @since 12/09/2017
 */
public class StringTableDomFileDescription extends DomFileDescription<StringTableProject> {
	private static String PROJECT = "Project";

	public StringTableDomFileDescription() {
		super(StringTableProject.class, PROJECT, "");
	}
}
