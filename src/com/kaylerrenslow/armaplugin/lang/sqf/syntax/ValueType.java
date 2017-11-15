package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 06/12/2016
 */
public enum ValueType {
	ANYTHING("Anything"),
	ARRAY("Array", true),
	ARRAY_OF_EDEN_ENTITIES("Array of Eden Entities", true),
	BOOLEAN("Boolean"),
	CODE("Code"),
	COLOR("Color", true),
	COLOR_RGB("Color RGB", true),
	CONFIG("Config"),
	CONTROL("Control"),
	DIARY_RECORD("Diary Record"),
	DISPLAY("Display"),
	EDEN_ENTITY("Eden Entity", true),
	EXCEPTION_TYPE("Exception Type"),
	GROUP("Group"),
	LOCATION("Location"),
	NAMESPACE("Namespace"),
	NET_OBJECT("NetObject"),
	NIL("nil"),
	NUMBER("Number"),
	NOTHING("Nothing"),
	OBJECT("Object"),
	OBJECT_RTD("ObjectRTD"),
	ORIENT("Orient"),
	ORIENTATION("Orientation"),
	POSITION("Position", true),
	POSITION_2D("Position 2D", true),
	POSITION_3D("Position 3D", true),
	POSITION_ASL("Position ASL", true),
	POSITION_ASLW("Position ASLW", true),
	POSITION_ATL("Position ATL", true),
	POSITION_AGL("Position AGL", true),
	POSITION_AGLS("Position AGLS", true),
	POSITION_WORLD("Position World", true),
	POSITION_RELATIVE("Position Relative", true),
	POSITION_CONFIG("Position Config", true),
	SCRIPT_HANDLE("Script (Handle)"),
	SIDE("Side"),
	STRING("String"),
	STRUCTURED_TEXT("Structured Text"),
	TARGET("Target"),
	TASK("Task"),
	TEAM("Team"),
	TEAM_MEMBER("Team Member"),
	TRANS("Trans"),
	TRANSFORMATION("Transformation"),
	WAYPOINT("Waypoint", true),
	VECTOR_3D("Vector 3D", true),
	VOID("Void"),

	/*fake types*/
	IF("If Type"),
	FOR("For Type"),
	SWITCH("Switch Type"),
	WHILE("While Type"),
	WITH("With Type"),;

	private final String displayName;
	private final boolean isArray;

	ValueType(String displayName) {
		this.displayName = displayName;
		isArray = false;
	}

	ValueType(String displayName, boolean isSimplifiedArray) {
		this.displayName = displayName;
		this.isArray = isSimplifiedArray;
	}

	@Override
	public String toString() {
		return displayName;
	}

	@NotNull
	public String getDisplayName() {
		return displayName;
	}

	public boolean isArray() {
		return isArray;
	}
}
