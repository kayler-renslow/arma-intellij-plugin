package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import com.kaylerrenslow.armaplugin.util.EmptyList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link ValueType} that is hard equal to {@link BaseType#CODE} and {@link BaseType#CODE} is hard equal to this (mathematical symmetric property),
 * but can also specify another {@link ValueType}
 * that the {@link BaseType#CODE} should return. For example, {0} is <code>new CodeType({@link BaseType#NUMBER})</code>
 * and {{}} can be either <code>new CodeType({@link BaseType#CODE})</code> or <code>new CodeType(new CodeType({@link BaseType#NOTHING}))</code>
 * <p>
 * Note that {@link BaseType#CODE} is equivalent to an instance of {@link CodeType} with {@link #getReturnType()} being
 * {@link BaseType#ANYTHING} or {@link BaseType#_VARIABLE}.
 *
 * @author kayler
 * @since 12/14/17
 */
public class CodeType extends ValueType {

	private final ValueType returnType;
	private final ExpandedValueType expandedValueType;
	private final List<ValueType> polymorphTypes = new ArrayList<>();

	public CodeType(@NotNull ValueType returnType) {
		this.returnType = returnType;
		expandedValueType = new ExpandedValueType(false, polymorphTypes, this);
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "{" + returnType.getDisplayName() + "}";
	}

	@Override
	public boolean isArray() {
		return BaseType.CODE.isArray();
	}

	@NotNull
	@Override
	public ExpandedValueType getExpanded() {
		return expandedValueType;
	}

	/**
	 * NOTE: DO NOT REMOVE {@link BaseType#CODE} from this list.
	 * It will break the mathematical symmetric property for {@link BaseType#CODE} being hard equal to this!
	 */
	@NotNull
	@Override
	public List<ValueType> getPolymorphicTypes() {
		return polymorphTypes;
	}

	@NotNull
	@Override
	public String getType() {
		return "{" + returnType.getType() + "}";
	}

	@NotNull
	public ValueType getReturnType() {
		return returnType;
	}


	/**
	 * Does the following checks in the following order:
	 * <ol>
	 * <li>If t is an instance of {@link CodeType}, will return true if and only if both {@link #getReturnType()} are hard equal.
	 * However, if both {@link #getReturnType()} passed into {@link ValueType#isAnythingOrVariable(ValueType, ValueType)}
	 * returns true, this will return true. If you are wondering why, it is the equivalent of {@link BaseType#CODE}
	 * because it can be anything or nothing at all.
	 * </li>
	 * <li>If t is hard equal to {@link BaseType#CODE}</li>
	 * </ol>
	 */
	@Override
	public boolean isHardEqual(@NotNull ValueType t) {
		if (t instanceof CodeType) {
			CodeType other = (CodeType) t;
			if (returnType.isAnythingOrVariable() || other.returnType.isAnythingOrVariable()) {
				return true;
			}
			return returnType.isHardEqual(other.returnType);
		}
		if (t.isHardEqual(BaseType.CODE)) {
			return true;
		}
		return false;
	}

}
