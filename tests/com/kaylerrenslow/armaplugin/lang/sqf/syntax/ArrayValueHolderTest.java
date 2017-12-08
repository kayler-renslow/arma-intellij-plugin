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
			Param firstArrayParam = new Param("p1", ValueType.BaseType.NUMBER, "", true);
			firstArrayParam.getType().getPolymorphicTypes().add(ValueType.BaseType.CONTROL);

			Param lastArrayParam = new Param("p2", ValueType.BaseType.BOOLEAN, "", true);
			lastArrayParam.getType().getPolymorphicTypes().add(ValueType.BaseType.CODE);
			lastArrayParam.getType().getPolymorphicTypes().add(ValueType.BaseType.CONFIG);

			p = new ArrayParam(false, Arrays.asList(
					firstArrayParam,
					lastArrayParam
			));
		}

		ExpandedValueType expect1 = new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.BOOLEAN);
		ExpandedValueType expect2 = new ExpandedValueType(false, ValueType.BaseType.CONTROL, ValueType.BaseType.BOOLEAN);
		ExpandedValueType expect3 = new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.CODE);
		ExpandedValueType expect4 = new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.CONFIG);
		ExpandedValueType expect5 = new SingletonArrayExpandedValueType(ValueType.BaseType.NUMBER);
		ExpandedValueType expect6 = new SingletonArrayExpandedValueType(ValueType.BaseType.CONTROL);
		ExpandedValueType expect7 = new ExpandedValueType(false);
		ExpandedValueType expect8 = new ExpandedValueType(false, ValueType.BaseType.CONTROL, ValueType.BaseType.CONFIG);

		ExpandedValueType[] allExpected = {expect1, expect2, expect3, expect4, expect5, expect6, expect7, expect8};

		for (ExpandedValueType expected : allExpected) {
			assertEquals("Expected to contain type " + expected.toString(), true, ValueType.typeEquivalent(p.getType(), expect2));
		}
	}

}