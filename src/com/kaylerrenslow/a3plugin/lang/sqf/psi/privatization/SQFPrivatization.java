package com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization;

import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import org.jetbrains.annotations.NotNull;

/**
 Created by Kayler on 05/05/2016.
 */
public abstract class SQFPrivatization {
	private final SQFVariable privateElement;
	private final SQFPrivatizer privatizer;

	/**
	 A private declaration is something that can be declared private inside a given scope. Example is a variable

	 @param privateElement the element that is being declared private
	 @param privatizer type of privatization
	 */
	private SQFPrivatization(@NotNull SQFVariable privateElement, @NotNull SQFPrivatizer privatizer) {
		this.privateElement = privateElement;
		this.privatizer = privatizer;
	}

	/**
	 Get the element that is being declared private
	 */
	@NotNull
	public SQFVariable getPrivateElement() {
		return privateElement;
	}

	/** Get the type of privatization */
	@NotNull
	public SQFPrivatizer getPrivatizer() {
		return privatizer;
	}

}
