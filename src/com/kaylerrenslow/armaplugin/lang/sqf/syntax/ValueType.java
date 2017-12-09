package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Kayler
 * @since 06/12/2016
 */
public abstract class ValueType {

	/**
	 * This method will compare {@link ExpandedValueType} instances returned from {@link ValueType#getExpanded()} and
	 * {@link #getPolymorphicTypes()}.
	 * <p>
	 * For comparing {@link ExpandedValueType} instances, type2's {@link ExpandedValueType} must have >= number
	 * of elements to type1's number of elements. Also, each element type must match at each index. If an array
	 * type is in the array type, this comparison will be used recursively.
	 * <p>
	 * If an allowed type is equal to {@link BaseType#ANYTHING} or <code>type</code> is {@link BaseType#ANYTHING},
	 * the comparison of {@link ValueType} instances will always be true. Also, this method will treat {@link BaseType#_VARIABLE}
	 * like it is {@link BaseType#ANYTHING}.
	 *
	 * @param type1 type
	 * @param type2 other type to check
	 * @return true if types are equivalent, false otherwise
	 * @see #equivalentByPolymorphicTypes(ValueType, ValueType)
	 */
	public static boolean typeEquivalent(@NotNull ValueType type1, @NotNull ValueType type2) {
		if (!(type1.isArray() && type2.isArray()) && (type1.isArray() || type2.isArray())) {
			//both must be arrays
			return false;
		}

		if (type1.isHardEqual(type2)) {
			return true;
		}

		final boolean type1IsPoly = type1 instanceof PolymorphicWrapperValueType;
		final boolean type2IsPoly = type2 instanceof PolymorphicWrapperValueType;

		if (type1IsPoly || type2IsPoly) {
			ValueType unwrappedType1 = type1;
			ValueType unwrappedType2 = type2;
			if (type1IsPoly) {
				unwrappedType1 = ((PolymorphicWrapperValueType) type1).getWrappedValueType();
			}
			if (type2IsPoly) {
				unwrappedType2 = ((PolymorphicWrapperValueType) type2).getWrappedValueType();
			}
			if (typeEquivalent(unwrappedType1, unwrappedType2)) {
				return true;
			}
		}

		if (isAnythingOrVariable(type1, type2)) {
			return true;
		}

		if (equivalentByPolymorphicTypes(type1, type2)) {
			return true;
		}

		//check expanded types

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
			return (
					type1Expanded.isEmptyArray()
							|| type1IsUnboundedEmpty
							|| type1Expanded.getNumOptionalValues() >= type1Expanded.getValueTypes().size()
			) && (
					type2Expanded.isEmptyArray()
							|| type2IsUnboundedEmpty
			);
		}

		ValueType lastType1 = BaseType.NOTHING, lastType2 = BaseType.NOTHING;

		if (type1IsUnboundedEmpty) {
			qType1.add(BaseType.ANYTHING);
			lastType1 = qType1.getFirst();
		} else {
			List<ValueType> types = type1Expanded.getValueTypes();
			for (ValueType t : types) {
				qType1.add(t);
				lastType1 = t;
			}
		}

		if (type2IsUnboundedEmpty) {
			qType2.add(BaseType.ANYTHING);
			lastType2 = qType2.getFirst();
		} else {
			List<ValueType> types = type2Expanded.getValueTypes();
			for (ValueType t : types) {
				qType2.add(t);
				lastType2 = t;
			}
		}

		while (!qType1.isEmpty() || (type1IsUnbounded && !qType2.isEmpty())) {
			ValueType type1Pop = (qType1.isEmpty() && type1IsUnbounded) ? lastType1 : qType1.removeFirst();
			ValueType type2Pop = (qType2.isEmpty() && type2IsUnbounded) ? lastType2 :
					(qType2.isEmpty() ? null : qType2.removeFirst());

			if (type2Pop == null) {
				//check if remaining qType1 values are optional

				if (type1Expanded.getNumOptionalValues() <= 0) {
					return false;
				}
				if (qType1.size() >= type1Expanded.getNumOptionalValues()) {
					return false;
				}

				//we can omit last values because they are optional
				return true;
			}
			if (type1Pop.isArray() || type2Pop.isArray()) {
				if (!typeEquivalent(type1Pop, type2Pop)) {
					return false;
				}
			} else {
				if (isAnythingOrVariable(type1Pop, type2Pop)) {
					continue;
				}
				if (!type1Pop.isHardEqual(type2Pop)) {
					if (!equivalentByPolymorphicTypes(type1Pop, type2Pop)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static boolean isAnythingOrVariable(@NotNull ValueType type1, @NotNull ValueType type2) {
		return type1.isHardEqual(BaseType.ANYTHING)
				|| type1.isHardEqual(BaseType._VARIABLE)
				|| type2.isHardEqual(BaseType.ANYTHING)
				|| type2.isHardEqual(BaseType._VARIABLE);
	}

	/**
	 * Checks only {@link ValueType#getPolymorphicTypes()} for each type. For each poly type, {@link #typeEquivalent(ValueType, ValueType)}
	 * will be used to check if they are equal
	 *
	 * @return true if the types are equal, false if they aren't
	 */
	public static boolean equivalentByPolymorphicTypes(@NotNull ValueType type1, @NotNull ValueType type2) {
		final boolean noType1Poly = type1.getPolymorphicTypes().isEmpty();
		final boolean noType2Poly = type2.getPolymorphicTypes().isEmpty();
		if (noType1Poly) {
			if (noType2Poly) {
				//nothing left to check
				return false;
			}
			boolean found = false;
			for (ValueType polyType2 : type2.getPolymorphicTypes()) {
				if (typeEquivalent(type1, polyType2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		} else {
			if (noType2Poly) {
				boolean found = false;
				for (ValueType polyType1 : type1.getPolymorphicTypes()) {
					if (typeEquivalent(polyType1, type2)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			} else {
				boolean found = false;
				for (ValueType polyType1 : type1.getPolymorphicTypes()) {
					for (ValueType polyType2 : type2.getPolymorphicTypes()) {
						if (typeEquivalent(polyType1, polyType2)) {
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}
		return true;

	}

	@NotNull
	public abstract String getDisplayName();

	public abstract boolean isArray();

	@NotNull
	public abstract ExpandedValueType getExpanded();

	/**
	 * @return a mutable list of other {@link ValueType} this type can represent
	 */
	@NotNull
	public abstract List<ValueType> getPolymorphicTypes();

	/**
	 * A String that is used for comparison in {@link #isHardEqual(ValueType)}. You can think of this as like a "class name"
	 * where a class is equal to another class by checking it's full name (java.lang.String for example).
	 *
	 * @return String
	 */
	@NotNull
	public abstract String getType();

	/**
	 * @return {@link #typeEquivalent(ValueType, ValueType)} with this as first parameter and other as second parameter
	 */
	public boolean typeEquivalent(@NotNull ValueType other) {
		return typeEquivalent(this, other);
	}

	/**
	 * @return the class name with {@link #getDisplayName()} inside it
	 */
	@NotNull
	public String getDebugName() {
		return getClass().getName() + "{" + getDisplayName() + "}";
	}

	/**
	 * This is different from {@link #typeEquivalent(ValueType)} in that this is called inside {@link #typeEquivalent(ValueType)}
	 * to check when {@link BaseType} are equal. Default implementation checks if this==other or this.getType.equals(other.getType)
	 * or {@link #getPolymorphicTypes()} contains other.
	 * <p>
	 * You can override this method for where you may be wrapping a {@link BaseType} instance or you don't want to
	 * compare by {@link #getType()}
	 *
	 * @return true if this type is equal to other.
	 */
	public boolean isHardEqual(@NotNull ValueType other) {
		return this == other || this.getType().equals(other.getType())
				|| getPolymorphicTypes().contains(other);
	}

	/**
	 * @return {@link #isHardEqual(ValueType)} result, or false if obj isn't a {@link ValueType} instance
	 */
	@Override
	public final boolean equals(Object obj) {
		return obj == this || (obj instanceof ValueType && this.isHardEqual((ValueType) obj));
	}

	public static class BaseType extends ValueType {
		public static final BaseType ANYTHING = new BaseType("ANYTHING", "Anything");
		public static final BaseType ARRAY = new BaseType("ARRAY", "Array", new ExpandedValueType(true));
		public static final BaseType ARRAY_OF_EDEN_ENTITIES = new BaseType("ARRAY_OF_EDEN_ENTITIES", "Array of Eden Entities",
				new Function<>() {
					@Override
					public ExpandedValueType apply(Void aVoid) {
						return new ExpandedValueType(
								new ExpandedValueType(true, BaseType.OBJECT),
								new ExpandedValueType(true, BaseType.GROUP),
								new ExpandedValueType(true, BaseType.OBJECT),
								new ExpandedValueType(true, BaseType.OBJECT),
								new ExpandedValueType(true, BaseType.WAYPOINT.getExpanded()),
								new ExpandedValueType(true, BaseType.STRING),
								new ExpandedValueType(true, BaseType.NUMBER),
								new ExpandedValueType(true, BaseType.NUMBER)
						);
					}
				}
		);
		public static final BaseType BOOLEAN = new BaseType("BOOLEAN", "Boolean");
		public static final BaseType CODE = new BaseType("CODE", "Code");
		public static final BaseType COLOR = new BaseType("COLOR", "Color", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType COLOR_RGB = new BaseType("COLOR_RGB", "Color RGB", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType CONFIG = new BaseType("CONFIG", "Config");
		public static final BaseType CONTROL = new BaseType("CONTROL", "Control");
		public static final BaseType DIARY_RECORD = new BaseType("DIARY_RECORD", "Diary Record");
		public static final BaseType DISPLAY = new BaseType("DISPLAY", "Display");
		public static final BaseType EDEN_ENTITY = new BaseType("EDEN_ENTITY", "Eden Entity");
		public static final BaseType EXCEPTION_TYPE = new BaseType("EXCEPTION_TYPE", "Exception Type");
		public static final BaseType GROUP = new BaseType("GROUP", "Group");
		public static final BaseType LOCATION = new BaseType("LOCATION", "Location");
		public static final BaseType NAMESPACE = new BaseType("NAMESPACE", "Namespace");
		public static final BaseType NET_OBJECT = new BaseType("NET_OBJECT", "NetObject");
		public static final BaseType NIL = new BaseType("NIL", "nil");
		public static final BaseType NUMBER = new BaseType("NUMBER", "Number");
		public static final BaseType NOTHING = new BaseType("NOTHING", "Nothing");
		public static final BaseType OBJECT = new BaseType("OBJECT", "Object");
		public static final BaseType OBJECT_RTD = new BaseType("OBJECT_RTD", "ObjectRTD");
		public static final BaseType ORIENT = new BaseType("ORIENT", "Orient");
		public static final BaseType ORIENTATION = new BaseType("ORIENTATION", "Orientation");
		public static final BaseType POSITION = new BaseType("POSITION", "Position", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(false,
						BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER
				);
			}
		});
		public static final BaseType POSITION_2D = new BaseType("POSITION_2D", "Position 2D", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_3D = new BaseType("POSITION_3D", "Position 3D", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ASL = new BaseType("POSITION_ASL", "Position ASL", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ASLW = new BaseType("POSITION_ASLW", "Position ASLW", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ATL = new BaseType("POSITION_ATL", "Position ATL", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_AGL = new BaseType("POSITION_AGL", "Position AGL", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_AGLS = new BaseType("POSITION_AGLS", "Position AGLS", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_WORLD = new BaseType("POSITION_WORLD", "Position World", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_RELATIVE = new BaseType("POSITION_RELATIVE", "Position Relative", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_CONFIG = new BaseType("POSITION_CONFIG", "Position Config", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType SCRIPT_HANDLE = new BaseType("SCRIPT_HANDLE", "Script (Handle)");
		public static final BaseType SIDE = new BaseType("SIDE", "Side");
		public static final BaseType STRING = new BaseType("STRING", "String");
		public static final BaseType STRUCTURED_TEXT = new BaseType("STRUCTURED_TEXT", "Structured Text");
		public static final BaseType TARGET = new BaseType("TARGET", "Target");
		public static final BaseType TASK = new BaseType("TASK", "Task");
		public static final BaseType TEAM = new BaseType("TEAM", "Team");
		public static final BaseType TEAM_MEMBER = new BaseType("TEAM_MEMBER", "Team Member");
		public static final BaseType TRANS = new BaseType("TRANS", "Trans");
		public static final BaseType TRANSFORMATION = new BaseType("TRANSFORMATION", "Transformation");
		public static final BaseType WAYPOINT = new BaseType("WAYPOINT", "Waypoint", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.GROUP, BaseType.NUMBER);
			}
		});
		public static final BaseType VECTOR_3D = new BaseType("VECTOR_3D", "Vector 3D", new Function<>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType VOID = new BaseType("VOID", "Void");

		/*fake types*/
		public static final BaseType IF = new BaseType("IF", "If Type");
		public static final BaseType FOR = new BaseType("FOR", "For Type");
		public static final BaseType SWITCH = new BaseType("SWITCH", "Switch Type");
		public static final BaseType WHILE = new BaseType("WHILE", "While Type");
		public static final BaseType WITH = new BaseType("WITH", "With Type");

		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a variable is being used
		 * and that the type is indeterminate with static type checking.
		 */
		public static final BaseType _VARIABLE = new BaseType("_VARIABLE", "`VARIABLE`");
		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a type couldn't be determined because of an error.
		 */
		public static final BaseType _ERROR = new BaseType("_ERROR", "Generic Error");

		private final String type;
		private final String displayName;
		private Function<Void, ExpandedValueType> getExpandedFunc;
		/**
		 * DO NOT ACCESS THIS DIRECTLY. USE {@link #getExpanded()}
		 */
		private ExpandedValueType expandedValueType;

		BaseType(String type, String displayName) {
			this.displayName = displayName;
			this.expandedValueType = new ExpandedValueType(this);
			this.type = type;
		}

		BaseType(String type, String displayName, Function<Void, ExpandedValueType> getExpandedFunc) {
			this.displayName = displayName;
			this.getExpandedFunc = getExpandedFunc;
			this.type = type;
		}

		BaseType(String type, String displayName, ExpandedValueType expandedValueType) {
			this.type = type;
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
			if (expandedValueType == null) {
				expandedValueType = getExpandedFunc.apply(null);
			}
			return expandedValueType;
		}

		/**
		 * @return a mutable list that won't persist for this lookup value type
		 */
		@NotNull
		@Override
		public List<ValueType> getPolymorphicTypes() {
			return new ArrayList<>(); //list should be mutable according to api, but we don't want the base types to be polymorphic
		}

		@NotNull
		@Override
		public String getType() {
			return type;
		}

		@Nullable
		public static ValueType valueOf(@NotNull String type) {
			try {
				return (ValueType) BaseType.class.getField(type).get(null);
			} catch (Exception e) {
				return null;
			}
		}
	}

}
