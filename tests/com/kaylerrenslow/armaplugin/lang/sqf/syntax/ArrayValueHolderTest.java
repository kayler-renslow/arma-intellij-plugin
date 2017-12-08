package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Kayler
 * @since 12/08/2017
 */
public class ArrayValueHolderTest {
	@Test
	public void allowedAlternateTypesContains() throws Exception {
		//test if array param has alternate types by also checking in non-Array params alternate types
		ArrayParam p;
		{
			Param firstArrayParam = new Param("p1", ValueType.Lookup.NUMBER, "", true);
			firstArrayParam.getAlternateValueTypes().add(ValueType.Lookup.CONTROL);

			Param lastArrayParam = new Param("p2", ValueType.Lookup.BOOLEAN, "", true);
			lastArrayParam.getAlternateValueTypes().add(ValueType.Lookup.CODE);
			lastArrayParam.getAlternateValueTypes().add(ValueType.Lookup.CONFIG);

			p = new ArrayParam(false, Arrays.asList(
					firstArrayParam,
					lastArrayParam
			));
			p.getAlternateValueTypes().add(new ExpandedValueType(false, ValueType.Lookup.NUMBER, ValueType.Lookup.STRING));
		}

		ExpandedValueType expect1 = new ExpandedValueType(false, ValueType.Lookup.NUMBER, ValueType.Lookup.BOOLEAN);
		ExpandedValueType expect2 = new ExpandedValueType(false, ValueType.Lookup.NUMBER, ValueType.Lookup.STRING);
		ExpandedValueType expect3 = new ExpandedValueType(false, ValueType.Lookup.NUMBER, ValueType.Lookup.CODE);
		ExpandedValueType expect4 = new ExpandedValueType(false, ValueType.Lookup.NUMBER, ValueType.Lookup.CONFIG);
		ExpandedValueType expect5 = new SingletonArrayExpandedValueType(ValueType.Lookup.NUMBER);
		ExpandedValueType expect6 = new SingletonArrayExpandedValueType(ValueType.Lookup.CONTROL);
		ExpandedValueType expect7 = new ExpandedValueType(false);

		ExpandedValueType[] allExpected = {expect1, expect2, expect3, expect4, expect5, expect6, expect7};

		for (ExpandedValueType expected : allExpected) {
			assertEquals("Expected to contain type " + expected.toString(), true, p.allowedTypesContains(expected));
		}
	}
}