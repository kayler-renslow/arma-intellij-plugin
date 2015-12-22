package com.kaylerrenslow.plugin;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFLanguage extends Language{
    public static final SQFLanguage INSTANCE = new SQFLanguage();
	private static final List<Language> dialects = new ListUtil<Language>().createList();

    public SQFLanguage(){
        super(Static.NAME);
	}

	@NotNull
	@Override
	public List<Language> getDialects() {
		return dialects;
	}
}
