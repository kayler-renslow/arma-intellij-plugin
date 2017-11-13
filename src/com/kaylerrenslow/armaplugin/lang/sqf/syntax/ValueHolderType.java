package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by Kayler on 06/12/2016.
 */
public enum ValueHolderType {
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

	public final String displayName;

	ValueHolderType(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}

	private static final HashMap<String, ValueHolderType> remap = new HashMap<>();

	static {
		remap.put("Editor Object", STRING);
		remap.put("Strings", STRING);
		remap.put("Objects", OBJECT);
		remap.put("Numbers", NUMBER);
		remap.put("Bool", BOOLEAN);
		remap.put("bool", BOOLEAN);
		remap.put("Eden Entities", ARRAY_OF_EDEN_ENTITIES);
		remap.put("Nil", NIL);
		remap.put("Code String", STRING);
		remap.put("Integer", NUMBER);
		remap.put("Any", ANYTHING);
		remap.put("Any Value", ANYTHING);
		remap.put("Script", SCRIPT_HANDLE);
		remap.put("Script_(Handle)", SCRIPT_HANDLE);
		remap.put("Scalar", NUMBER);
		remap.put("PositionASL", POSITION_ASL);
		remap.put("PositionWorld", POSITION_WORLD);
	}

	@Nullable
	public static ValueHolderType getFromDisplayName(String displayName) {
		for (ValueHolderType valueType : values()) {
			if (valueType.displayName.equals(displayName)) {
				return valueType;
			}
		}
		return remap.get(displayName);
	}

	@NotNull
	public String getDisplayName() {
		return displayName;
	}
}
