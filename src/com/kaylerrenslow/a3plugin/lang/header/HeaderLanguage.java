package com.kaylerrenslow.a3plugin.lang.header;

import com.intellij.lang.Language;

/**
 * Created by Kayler on 10/31/2015.
 */
public class HeaderLanguage extends Language{
    public static final HeaderLanguage INSTANCE = new HeaderLanguage();

    public HeaderLanguage(){
        super(HeaderStatic.NAME);
	}


}
