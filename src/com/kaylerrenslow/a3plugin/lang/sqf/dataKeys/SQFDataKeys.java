package com.kaylerrenslow.a3plugin.lang.sqf.dataKeys;

import com.intellij.openapi.util.Key;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFScope;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.SQFVariable;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization.SQFPrivatization;
import com.kaylerrenslow.a3plugin.lang.sqf.psi.wrapper.SQFPrivateDeclVar;

import java.util.List;

/**
 @author Kayler
 Data Keys for SQF language
 Created on 07/12/2016. */
public class SQFDataKeys {
	/** Used for getting an SQFVariable's declaration scope. If null, use {@link SQFVariable#getDeclarationScope()} to cache it in this key.
	 However, calling the method will either return this Key's value or update the key's value and return it. */
	public static final Key<SQFScope> DECLARATION_SCOPE = new Key<>("armaPlugin.variable.declarationScope");

	/** Used for getting an SQFVariable's privatization. If null, use {@link SQFVariable#getPrivatization()} to cache it in this key.
	 However, calling the method will either return this Key's value or update the key's value and return it. */
	public static final Key<SQFPrivatization> PRIVATIZATION = new Key<>("armaPlugin.variable.privatization");

	/** Used for getting an SQFScope's private declared variables. If null, use {@link SQFScope#getPrivateVars()} to cache it in this key.
	 However, calling the method will either return this Key's value or update the key's value and return it. */
	public static final Key<List<SQFPrivateDeclVar>> PRIVATE_DECL_VARS = new Key<>("armaPlugin.variable.privateDeclVars");
}
