package com.kaylerrenslow.a3plugin.lang.sqf;

import com.intellij.lang.Language;

/**
 * @author Kayler
 * Language extension point for SQF language
 * Created on 10/31/2015.
 */
public class SQFLanguage extends Language{
    public static final SQFLanguage INSTANCE = new SQFLanguage();

    public SQFLanguage(){
        super(SQFStatic.NAME);
	}

}
