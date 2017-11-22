package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 06/12/2016
 */
public interface ValueType {

	/**
	 * This method will compare {@link ExpandedValueType} instances returned from {@link ValueType#getExpanded()}.
	 * <p>
	 * For comparing {@link ExpandedValueType} instances, type2's {@link ExpandedValueType} must have >= number
	 * of elements to type1's number of elements. Also, each element type must match at each index. If an array
	 * type is in the array type, this comparison will be used recursively.
	 * <p>
	 * If an allowed type is equal to {@link Lookup#ANYTHING} or <code>type</code> is {@link Lookup#ANYTHING},
	 * the comparison of {@link ValueType} instances will always be true.
	 *
	 * @param type1 type to check
	 * @param type2 other type to check
	 * @return true if types are equivalent, false otherwise
	 * @throws IllegalArgumentException if {@link ExpandedValueType#isEmptyArray()} returns true for either provided type
	 */
	static boolean typeEquivalent(@NotNull ValueType type1, @NotNull ValueType type2) {

		LinkedList<ValueType> qType1 = new LinkedList<>();
		LinkedList<ValueType> qType2 = new LinkedList<>();

		ExpandedValueType type1Expanded = type1.getExpanded();
		ExpandedValueType type2Expanded = type2.getExpanded();

		final boolean type1IsUnbounded = type1Expanded.isUnbounded();
		final boolean type2IsUnbounded = type2Expanded.isUnbounded();
		final boolean type1IsUnboundedEmpty = type1IsUnbounded && type1Expanded.getValueTypes().isEmpty();
		final boolean type2IsUnboundedEmpty = type2IsUnbounded && type2Expanded.getValueTypes().isEmpty();

		if (type1IsUnboundedEmpty && type2IsUnboundedEmpty) {
			return true;
		}

		if (type1Expanded.isEmptyArray() || type2Expanded.isEmptyArray()) {
			return (type1Expanded.isEmptyArray() || type1IsUnboundedEmpty)
					&& (type2Expanded.isEmptyArray() || type2IsUnboundedEmpty);
		}

		if (!(type1.isArray() && type2.isArray()) && (type1.isArray() || type2.isArray())) {
			return false;
		}

		if (type1Expanded.getValueTypes().size() < type2Expanded.getValueTypes().size()
				&& !type1IsUnbounded && !type2IsUnbounded) {
			return false;
		}

		ValueType lastType1 = Lookup.NOTHING, lastType2 = Lookup.NOTHING;

		if (type1IsUnboundedEmpty) {
			qType1.add(Lookup.ANYTHING);
			lastType1 = qType1.getFirst();
		} else {
			List<ValueType> types = type1Expanded.getValueTypes();
			for (ValueType t : types) {
				qType1.add(t);
				lastType1 = t;
			}
		}

		if (type2IsUnboundedEmpty) {
			qType2.add(Lookup.ANYTHING);
			lastType2 = qType2.getFirst();
		} else {
			List<ValueType> types = type2Expanded.getValueTypes();
			for (ValueType t : types) {
				qType2.add(t);
				lastType2 = t;
			}
		}

		while (!qType1.isEmpty() || (type1IsUnbounded && !qType2.isEmpty())) {
			ValueType type1Pop = (qType1.isEmpty() && type1IsUnbounded) ? lastType1 :
					(qType1.isEmpty() ? null : qType1.removeFirst());
			ValueType type2Pop = (qType2.isEmpty() && type2IsUnbounded) ? lastType2 :
					(qType2.isEmpty() ? null : qType2.removeFirst());

			if (type1Pop == null || type2Pop == null) {
				return false;
			}

			if (type1Pop == Lookup.ANYTHING) {
				continue;
			}
			if (type2Pop == Lookup.ANYTHING) {
				continue;
			}
			if (type1Pop.isArray()) {
				if (!type2Pop.isArray()) {
					return false;
				}
				boolean equal = typeEquivalent(type1Pop.getExpanded(), type2Pop.getExpanded());
				if (!equal) {
					return false;
				}
			} else {
				if (type2Pop.isArray()) {
					return false;
				}
				if (!type1Pop.equals(type2Pop)) {
					return false;
				}
			}
		}
		return true;
	}

	@NotNull
	String getDisplayName();

	boolean isArray();

	@NotNull
	ExpandedValueType getExpanded();

	enum Lookup implements ValueType {
		ANYTHING("Anything"),
		ARRAY("Array", new ExpandedValueType(true)),
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


		Lookup(String displayName, ExpandedValueType expandedValueType) {
			this.displayName = displayName;
			this.expandedValueType = expandedValueType;
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
