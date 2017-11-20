package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.Lookup;
import org.junit.Test;

import static com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.typeEquivalent;
import static org.junit.Assert.assertEquals;

/**
 * @author Kayler
 * @since 11/19/2017
 */
public class ValueTypeEquivalenceTest {
	@Test
	public void typeEqual_code() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.CODE, Lookup.CODE));
		assertEquals(true, typeEquivalent(Lookup.CODE, new ExpandedValueType(Lookup.CODE)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.CODE), Lookup.CODE));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.CODE), new ExpandedValueType(Lookup.CODE)));
	}

	@Test
	public void typeEqual_number() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.NUMBER, Lookup.NUMBER));
		assertEquals(true, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(Lookup.NUMBER)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.NUMBER), Lookup.NUMBER));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.NUMBER), new ExpandedValueType(Lookup.NUMBER)));
	}

	@Test
	public void typeEqual_object() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.OBJECT, Lookup.OBJECT));
		assertEquals(true, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(Lookup.OBJECT)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.OBJECT), Lookup.OBJECT));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.OBJECT), new ExpandedValueType(Lookup.OBJECT)));
	}

	@Test
	public void typeNotEqual_number() throws Exception {
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(Lookup.ARRAY)));
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(Lookup.CONFIG)));
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(Lookup.CODE)));
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(Lookup.OBJECT)));
	}

	@Test
	public void typeNotEqual_object() throws Exception {
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(Lookup.ARRAY)));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(Lookup.CONFIG)));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(Lookup.CODE)));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(Lookup.NUMBER)));
	}

	@Test
	public void typeEqual_array_empty() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.ARRAY, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), Lookup.ARRAY));
	}

	@Test
	public void typeNotEqual_array_empty() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.ARRAY, new ExpandedValueType(Lookup.ARRAY)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(Lookup.ARRAY), Lookup.ARRAY));
	}

	@Test
	public void typeEqual_emptyUnboundedArray() throws Exception {
		//these are true because an unbounded array with no elements provided counts as an array of infinitely many things or nothing

		assertEquals(true, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), Lookup.NUMBER));
		assertEquals(true, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), Lookup.OBJECT));
	}

	@Test
	public void typeNotEqual_emptyBoundedExpandedType() throws Exception {
		try {
			typeEquivalent(new ExpandedValueType(false), Lookup.NUMBER);
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}

		try {
			typeEquivalent(new ExpandedValueType(false), Lookup.OBJECT);
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}

		try {
			typeEquivalent(Lookup.NUMBER, new ExpandedValueType(false));
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}

		try {
			typeEquivalent(Lookup.OBJECT, new ExpandedValueType(false));
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}

		try {
			typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false));
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}

		try {
			typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false));
			assertEquals("Expected an exception", true, false);
		} catch (IllegalArgumentException ignore) {

		}
	}

	@Test
	public void typeEqual_notEmpty_singleton_unboundedArray() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(true, Lookup.NUMBER)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true, Lookup.NUMBER), Lookup.NUMBER));
		assertEquals(true, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true, Lookup.OBJECT), Lookup.OBJECT));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.OBJECT)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE),
				new ExpandedValueType(true, Lookup.CODE)
		));
	}

	@Test
	public void typeEqual_notEmpty_notSingleton_unboundedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT, Lookup.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE, Lookup.OBJECT, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.CODE, Lookup.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.NUMBER),
				new ExpandedValueType(true, Lookup.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.NUMBER),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT, Lookup.CODE)
		));

	}

}