package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016
 */
public class Syntax {
	private ReturnValueHolder returnValue;
	private Param prefixParam;
	private Param postfixParam;

	public Syntax(@Nullable Param prefixParam, @Nullable Param postfixParam, @NotNull ReturnValueHolder returnValue) {
		this.prefixParam = prefixParam;
		this.postfixParam = postfixParam;
		this.returnValue = returnValue;
	}

	@NotNull
	public static List<Param> params(@NotNull Param... allParams) {
		List<Param> params = new ArrayList<>(allParams.length);
		Collections.addAll(params, allParams);
		return params;
	}

	@NotNull
	public ReturnValueHolder getReturnValue() {
		return returnValue;
	}

	@Nullable
	public Param getPrefixParam() {
		return prefixParam;
	}

	@Nullable
	public Param getPostfixParam() {
		return postfixParam;
	}

	@NotNull
	public Iterable<Param> getAllParams() {
		List<Param> list = new LinkedList<>();
		if (prefixParam != null) {
			addAllParamsFor(list, prefixParam);
		}
		if (postfixParam != null) {
			addAllParamsFor(list, postfixParam);
		}

		return list;
	}

	private void addAllParamsFor(@NotNull List<Param> params, @NotNull Param param) {
		if (param instanceof ArrayParam) {
			ArrayParam arrayParam = (ArrayParam) param;
			for (Param subParam : arrayParam.getParams()) {
				if (subParam instanceof ArrayParam) {
					addAllParamsFor(params, subParam);
				} else {
					params.add(subParam);
				}
			}
		} else {
			params.add(param);
		}

	}

	@NotNull
	public Iterable<ArrayParam> getAllArrayParams() {
		List<ArrayParam> list = new LinkedList<>();
		if (prefixParam != null && prefixParam instanceof ArrayParam) {
			addAllArrayParamsFor(list, (ArrayParam) prefixParam);
		}
		if (postfixParam != null && postfixParam instanceof ArrayParam) {
			addAllArrayParamsFor(list, (ArrayParam) postfixParam);
		}
		return list;
	}

	private void addAllArrayParamsFor(@NotNull List<ArrayParam> params, @NotNull ArrayParam param) {
		for (Param subParam : param.getParams()) {
			if (subParam instanceof ArrayParam) {
				ArrayParam subArrayParam = (ArrayParam) subParam;
				params.add(subArrayParam);
				addAllArrayParamsFor(params, subArrayParam);
			}
		}
	}
}
