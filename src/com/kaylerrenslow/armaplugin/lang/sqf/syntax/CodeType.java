package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.BaseType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * A {@link ValueType} that is equivalent to {@link BaseType#CODE}, but can also specify another {@link ValueType}
 * that the {@link BaseType#CODE} should return. For example, {0} is <code>new CodeType({@link BaseType#NUMBER})</code>
 * and {{}} can be either <code>new CodeType({@link BaseType#CODE})</code> or <code>new CodeType(new CodeType({@link BaseType#NOTHING}))</code>
 *
 * @author kayler
 * @since 12/14/17
 */
public class CodeType extends ValueType {

	private final ValueType returnType;
	private final ExpandedValueType expandedValueType;

	public CodeType(@NotNull ValueType returnType) {
		this.returnType = returnType;
		expandedValueType = new ExpandedValueType(false, this);
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "{" + returnType.getDisplayName()+ "}";
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

	@NotNull
	@Override
	public List<ValueType> getPolymorphicTypes() {
		return Collections.emptyList();
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
	 *     <li>If t is an instance of {@link CodeType}, will return true if both {@link #getReturnType()} are hard equal</li>
	 *     <li>If t is hard equal to {@link BaseType#CODE}</li>
	 * </ol>
	 */
	@Override
	public boolean isHardEqual(@NotNull ValueType t) {
		if (t instanceof CodeType) {
			CodeType other = (CodeType) t;
			return returnType.isHardEqual(other.returnType);
		}
		if (t.isHardEqual(BaseType.CODE)) {
			return true;
		}
		return false;
	}
}
