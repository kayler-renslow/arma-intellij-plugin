package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016
 */
public class BasicValueHolder implements ValueHolder {

	private boolean optional;

	private final ValueHolderType type;
	private final String description;
	private final List<ValueHolderType> alternateValueTypes = new LinkedList<>();
	private final List<String> literals;

	public BasicValueHolder(@NotNull ValueHolderType type, @NotNull String description, boolean optional) {
		this(type, description, optional, new ArrayList<>());
	}

	public BasicValueHolder(@NotNull ValueHolderType type, @NotNull String description, boolean optional, @NotNull List<String> literals) {
		this.type = type;
		this.description = description.trim();
		this.optional = optional;
		this.literals = literals;
	}


	@Override
	@NotNull
	public ValueHolderType getType() {
		return type;
	}

	@Override
	@NotNull
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	@NotNull
	public List<ValueHolderType> getAlternateValueTypes() {
		return alternateValueTypes;
	}

	@NotNull
	public List<String> getLiterals() {
		return literals;
	}
}
