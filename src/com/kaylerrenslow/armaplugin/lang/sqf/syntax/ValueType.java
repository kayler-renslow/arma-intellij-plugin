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
public interface ValueType {

	/**
	 * This method will compare {@link ExpandedValueType} instances returned from {@link ValueType#getExpanded()}.
	 * <p>
	 * For comparing {@link ExpandedValueType} instances, type2's {@link ExpandedValueType} must have >= number
	 * of elements to type1's number of elements. Also, each element type must match at each index. If an array
	 * type is in the array type, this comparison will be used recursively.
	 * <p>
	 * If an allowed type is equal to {@link BaseType#ANYTHING} or <code>type</code> is {@link BaseType#ANYTHING},
	 * the comparison of {@link ValueType} instances will always be true. Also, this method will treat {@link BaseType#_VARIABLE}
	 * like it is {@link BaseType#ANYTHING}.
	 *
	 * @param type1 type to check
	 * @param type2 other type to check
	 * @return true if types are equivalent, false otherwise
	 * @throws IllegalArgumentException if {@link ExpandedValueType#isEmptyArray()} returns true for either provided type
	 */
	static boolean typeEquivalent(@NotNull ValueType type1, @NotNull ValueType type2) {
		if (type1.equals(BaseType.ANYTHING)
				|| type1.equals(BaseType._VARIABLE)
				|| type1.equals(BaseType.ANYTHING.getExpanded())
				|| type1.equals(BaseType._VARIABLE.getExpanded())) {
			return true;
		}

		if (type2.equals(BaseType.ANYTHING)
				|| type2.equals(BaseType._VARIABLE)
				|| type2.equals(BaseType.ANYTHING.getExpanded())
				|| type2.equals(BaseType._VARIABLE.getExpanded())) {
			return true;
		}

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

		if (!(type1.isArray() && type2.isArray()) && (type1.isArray() || type2.isArray())) {
			return false;
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
//				check if remaining qType1 values are optional

				if (type1Expanded.getNumOptionalValues() <= 0) {
					return false;
				}
				if (qType1.size() >= type1Expanded.getNumOptionalValues()) {
					return false;
				}

				//we can omit last values because they are optional
				return true;
			}

			if (type1Pop.equals(BaseType.ANYTHING) || type1Pop.equals(BaseType._VARIABLE)) {
				continue;
			}
			if (type2Pop.equals(BaseType.ANYTHING) || type2Pop.equals(BaseType._VARIABLE)) {
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
				continue;
			}

			if (type2Pop.isArray()) {
				//type1 must also be an array
				return false;
			}
			if (type1Pop.equals(type2Pop)) {
				continue;
			}
			//check polymorphic types
			final boolean noType1Poly = type1Pop.getPolymorphicTypes().isEmpty();
			final boolean noType2Poly = type2Pop.getPolymorphicTypes().isEmpty();
			if (noType1Poly) {
				if (noType2Poly) {
					//nothing left to check
					return false;
				}
				boolean found = false;
				for (ValueType polyType2 : type2Pop.getPolymorphicTypes()) {
					if (typeEquivalent(type1Pop, polyType2)) {
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
					for (ValueType polyType1 : type1Pop.getPolymorphicTypes()) {
						if (typeEquivalent(polyType1, type2Pop)) {
							found = true;
							break;
						}
					}
					if (!found) {
						return false;
					}
				} else {
					boolean found = false;
					for (ValueType polyType1 : type1Pop.getPolymorphicTypes()) {
						for (ValueType polyType2 : type2Pop.getPolymorphicTypes()) {
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
		}
		return true;
	}

	@NotNull
	String getDisplayName();

	boolean isArray();

	@NotNull
	ExpandedValueType getExpanded();

	/**
	 * @return a mutable list of other {@link ValueType} this type can represent
	 */
	@NotNull
	List<ValueType> getPolymorphicTypes();


	/**
	 * @return the class name with {@link #getDisplayName()} inside it
	 */
	@NotNull
	default String getDebugName() {
		return getClass().getName() + "{" + getDisplayName() + "}";
	}

	class BaseType implements ValueType {
		public static final BaseType ANYTHING = new BaseType("Anything");
		public static final BaseType ARRAY = new BaseType("Array", new ExpandedValueType(true));
		public static final BaseType ARRAY_OF_EDEN_ENTITIES = new BaseType("Array of Eden Entities",
				new Function<Void, ExpandedValueType>() {
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
		public static final BaseType BOOLEAN = new BaseType("Boolean");
		public static final BaseType CODE = new BaseType("Code");
		public static final BaseType COLOR = new BaseType("Color", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType COLOR_RGB = new BaseType("Color RGB", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType CONFIG = new BaseType("Config");
		public static final BaseType CONTROL = new BaseType("Control");
		public static final BaseType DIARY_RECORD = new BaseType("Diary Record");
		public static final BaseType DISPLAY = new BaseType("Display");
		public static final BaseType EDEN_ENTITY = new BaseType("Eden Entity");
		public static final BaseType EXCEPTION_TYPE = new BaseType("Exception Type");
		public static final BaseType GROUP = new BaseType("Group");
		public static final BaseType LOCATION = new BaseType("Location");
		public static final BaseType NAMESPACE = new BaseType("Namespace");
		public static final BaseType NET_OBJECT = new BaseType("NetObject");
		public static final BaseType NIL = new BaseType("nil");
		public static final BaseType NUMBER = new BaseType("Number");
		public static final BaseType NOTHING = new BaseType("Nothing");
		public static final BaseType OBJECT = new BaseType("Object");
		public static final BaseType OBJECT_RTD = new BaseType("ObjectRTD");
		public static final BaseType ORIENT = new BaseType("Orient");
		public static final BaseType ORIENTATION = new BaseType("Orientation");
		public static final BaseType POSITION = new BaseType("Position", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(false,
						BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER
				);
			}
		});
		public static final BaseType POSITION_2D = new BaseType("Position 2D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_3D = new BaseType("Position 3D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ASL = new BaseType("Position ASL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ASLW = new BaseType("Position ASLW", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_ATL = new BaseType("Position ATL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_AGL = new BaseType("Position AGL", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_AGLS = new BaseType("Position AGLS", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_WORLD = new BaseType("Position World", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_RELATIVE = new BaseType("Position Relative", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType POSITION_CONFIG = new BaseType("Position Config", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType SCRIPT_HANDLE = new BaseType("Script (Handle)");
		public static final BaseType SIDE = new BaseType("Side");
		public static final BaseType STRING = new BaseType("String");
		public static final BaseType STRUCTURED_TEXT = new BaseType("Structured Text");
		public static final BaseType TARGET = new BaseType("Target");
		public static final BaseType TASK = new BaseType("Task");
		public static final BaseType TEAM = new BaseType("Team");
		public static final BaseType TEAM_MEMBER = new BaseType("Team Member");
		public static final BaseType TRANS = new BaseType("Trans");
		public static final BaseType TRANSFORMATION = new BaseType("Transformation");
		public static final BaseType WAYPOINT = new BaseType("Waypoint", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.GROUP, BaseType.NUMBER);
			}
		});
		public static final BaseType VECTOR_3D = new BaseType("Vector 3D", new Function<Void, ExpandedValueType>() {
			@Override
			public ExpandedValueType apply(Void aVoid) {
				return new ExpandedValueType(BaseType.NUMBER, BaseType.NUMBER, BaseType.NUMBER);
			}
		});
		public static final BaseType VOID = new BaseType("Void");

		/*fake types*/
		public static final BaseType IF = new BaseType("If Type");
		public static final BaseType FOR = new BaseType("For Type");
		public static final BaseType SWITCH = new BaseType("Switch Type");
		public static final BaseType WHILE = new BaseType("While Type");
		public static final BaseType WITH = new BaseType("With Type");

		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a variable is being used
		 * and that the type is indeterminate with static type checking.
		 */
		public static final BaseType _VARIABLE = new BaseType("`VARIABLE`");
		/**
		 * Not an actual Arma 3 data type.
		 * This is for Arma Intellij Plugin to signify a type couldn't be determined because of an error.
		 */
		public static final BaseType _ERROR = new BaseType("Generic Error");

		private final String displayName;
		private Function<Void, ExpandedValueType> getExpandedFunc;
		/**
		 * DO NOT ACCESS THIS DIRECTLY. USE {@link #getExpanded()}
		 */
		private ExpandedValueType expandedValueType;

		BaseType(String displayName) {
			this.displayName = displayName;
			this.expandedValueType = new ExpandedValueType(this);
		}

		BaseType(String displayName, Function<Void, ExpandedValueType> getExpandedFunc) {
			this.displayName = displayName;
			this.getExpandedFunc = getExpandedFunc;
		}


		BaseType(String displayName, ExpandedValueType expandedValueType) {
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
			return new ArrayList<>(); //list should be mutable according to api, but we don't want the lookups to be polymorphic
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof PolymorphicWrapperValueType) {
				return obj.equals(this);
			}
			return false;
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
