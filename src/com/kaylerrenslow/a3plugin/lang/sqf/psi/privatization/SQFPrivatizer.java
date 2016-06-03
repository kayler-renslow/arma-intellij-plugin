package com.kaylerrenslow.a3plugin.lang.sqf.psi.privatization;

/**
 Created by Kayler on 06/03/2016.
 */
public enum SQFPrivatizer {
	/** Inherited privatization from being inside a spawn. Spawn command creates a new environment */
	SPAWN,
	/** Variable declared private by private "", private _var, or private []; */
	PRIVATE_COMMAND,
	/** Private via params[] */
	PARAMS_COMMAND
}
