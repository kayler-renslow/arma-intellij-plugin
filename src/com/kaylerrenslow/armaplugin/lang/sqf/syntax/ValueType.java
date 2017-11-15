package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kayler
 * @since 06/12/2016
 */
public enum ValueType {
	ANYTHING("Anything"),
	ARRAY("Array"),
	ARRAY_OF_EDEN_ENTITIES("Array of Eden Entities"),
	BOOLEAN("Boolean"),
	CODE("Code"),
	COLOR("Color"),
	COLOR_RGB("Color RGB"),
	CONFIG("Config"),
	CONTROL("Control"),
	DIARY_RECORD("Diary Record"),
	DISPLAY("Display"),
	EDEN_ENTITY("Eden Entity"),
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
	POSITION("Position"),
	POSITION_2D("Position 2D"),
	POSITION_3D("Position 3D"),
	POSITION_ASL("Position ASL"),
	POSITION_ASLW("Position ASLW"),
	POSITION_ATL("Position ATL"),
	POSITION_AGL("Position AGL"),
	POSITION_AGLS("Position AGLS"),
	POSITION_WORLD("Position World"),
	POSITION_RELATIVE("Position Relative"),
	POSITION_CONFIG("Position Config"),
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
	WAYPOINT("Waypoint"),
	VECTOR_3D("Vector 3D"),
	VOID("Void"),

	/*fake types*/
	IF("If Type"),
	FOR("For Type"),
	SWITCH("Switch Type"),
	WHILE("While Type"),
	WITH("With Type"),;

	private final String displayName;

	ValueType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

	@NotNull
	public String getDisplayName() {
		return displayName;
	}
}
