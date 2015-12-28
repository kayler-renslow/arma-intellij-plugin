package com.kaylerrenslow.plugin.lang.sqf;

import com.intellij.lang.Language;
import com.kaylerrenslow.plugin.ListUtil;
import com.kaylerrenslow.plugin.Static;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Kayler on 10/31/2015.
 */
public class SQFLanguage extends Language{
    public static final SQFLanguage INSTANCE = new SQFLanguage();

    public SQFLanguage(){
        super(SQFStatic.NAME);
	}

}
