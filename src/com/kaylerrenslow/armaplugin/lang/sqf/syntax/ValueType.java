package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author Kayler
 * @since 06/12/2016
 */
public interface ValueType {

	@NotNull
	String getDisplayName();

	boolean isArray();

	@NotNull
	ExpandedValueType getExpanded();

	enum Lookup implements ValueType {
		ANYTHING("Anything"),
		ARRAY("Array"),
		ARRAY_OF_EDEN_ENTITIES("Array of Eden Entities",
				new Function<Void, ExpandedValueType>() {
					@Override
					public ExpandedValueType apply(Void aVoid) {
						return new ExpandedValueType(
								new ExpandedValueType(true, Lookup.OBJECT),
								new ExpandedValueType(true, Lookup.GROUP),
								new ExpandedValueType(true, Lookup.OBJECT),
								new ExpandedValueType(true, Lookup.OBJECT),
								new ExpandedValueType(true, Lookup.WAYPOINT.getExpanded()),
								new ExpandedValueType(true, Lookup.STRING),
								new ExpandedValueType(true, Lookup.NUMBER),
								new ExpandedValueType(true, Lookup.NUMBER)
						);
					}
				}
		),
		BOOLEAN("Boolean"),
		CODE("Code"),
		COLOR("Color", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		COLOR_RGB("Color RGB", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
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
		POSITION("Position", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(false,
						Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER
				);
			}
		}),
		POSITION_2D("Position 2D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_3D("Position 3D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_ASL("Position ASL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_ASLW("Position ASLW", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_ATL("Position ATL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_AGL("Position AGL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_AGLS("Position AGLS", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_WORLD("Position World", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_RELATIVE("Position Relative", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		POSITION_CONFIG("Position Config", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
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
		WAYPOINT("Waypoint", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.GROUP, Lookup.NUMBER);
			}
		}),
		VECTOR_3D("Vector 3D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(Lookup.NUMBER, Lookup.NUMBER, Lookup.NUMBER);
			}
		}),
		VOID("Void"),

		/*fake types*/
		IF("If Type"),
		FOR("For Type"),
		SWITCH("Switch Type"),
		WHILE("While Type"),
		WITH("With Type"),

		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a variable is being used
		 * and that the type is indeterminate with static type checking.
		 */
		_VARIABLE("`VARIABLE`"),
		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a type couldn't be determined because of an error.
		 */
		_ERROR("Generic Error");

		private final String displayName;
		private Function<Void, ExpandedValueType> getExpandedFunc;
		private ExpandedValueType expandedValueType;

		Lookup(String displayName) {
			this.displayName = displayName;
			this.expandedValueType = new ExpandedValueType(this);
		}

		Lookup(String displayName, Function<Void, ExpandedValueType> getExpandedFunc) {
			this.displayName = displayName;
			this.getExpandedFunc = getExpandedFunc;
		}

		@Override
		public String toString() {
			return displayName;
		}

		@NotNull
		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public boolean isArray() {
			if (expandedValueType == null) {
				expandedValueType = getExpandedFunc.apply(null);
			}
			return expandedValueType.isArray();
		}

		@NotNull
		@Override
		public ExpandedValueType getExpanded() {
			return expandedValueType;
		}

	}

}
