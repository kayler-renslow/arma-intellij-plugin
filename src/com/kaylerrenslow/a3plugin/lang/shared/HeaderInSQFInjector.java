package com.kaylerrenslow.a3plugin.lang.shared;

import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 05/06/2016
 */
public class HeaderInSQFInjector implements LanguageInjector {
	@Override
	public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
		System.out.println("HeaderInSQFInjector.getLanguagesToInject");
		System.out.println(host.getContainingFile().getName());
		System.out.println("host = " + host);
		System.out.println("injectionPlacesRegistrar = " + injectionPlacesRegistrar);
	}
}
