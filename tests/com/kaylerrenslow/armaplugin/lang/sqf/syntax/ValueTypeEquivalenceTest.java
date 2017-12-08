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
		assertEquals(false, typeEquivalent(Lookup.ARRAY, new ExpandedValueType(Lookup.ARRAY)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(Lookup.ARRAY), Lookup.ARRAY));
	}

	@Test
	public void typeEqual_emptyUnboundedArray() throws Exception {
		//these are true because an unbounded array with no elements provided counts as an array of infinitely many things or nothing

		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true, Lookup.NUMBER), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true, Lookup.NUMBER)));
	}

	@Test
	public void typeNotEqual_emptyUnboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), Lookup.NUMBER));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), Lookup.OBJECT));
	}

	@Test
	public void typeEquivalence_emptyBoundedExpandedType() throws Exception {
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), Lookup.NUMBER));
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), Lookup.OBJECT));
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(false)));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(false)));

		assertEquals(true, typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(Lookup.ARRAY, new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(false), Lookup.ARRAY));
	}

	@Test
	public void typeEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.OBJECT)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE),
				new ExpandedValueType(true, Lookup.CODE)
		));

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
		assertEquals(false, typeEquivalent(Lookup.NUMBER, new ExpandedValueType(true, Lookup.NUMBER)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, Lookup.NUMBER), Lookup.NUMBER));
		assertEquals(false, typeEquivalent(Lookup.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, Lookup.OBJECT), Lookup.OBJECT));
	}

	@Test
	public void typeEqual_notEmpty_array() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, Lookup.NUMBER, Lookup.NUMBER),
				new ExpandedValueType(true, Lookup.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER),
				new ExpandedValueType(false, Lookup.NUMBER, Lookup.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, Lookup.NUMBER, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.NUMBER, Lookup.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_array() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER),
				new ExpandedValueType(true, Lookup.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER),
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.NUMBER, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.OBJECT, Lookup.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.NUMBER),
				new ExpandedValueType(false, Lookup.OBJECT)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE),
				new ExpandedValueType(false, Lookup.CODE, Lookup.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.CODE),
				new ExpandedValueType(false, Lookup.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.OBJECT),
				new ExpandedValueType(true, Lookup.OBJECT)
		));
	}

	@Test
	public void typeEqual_singleton() throws Exception {
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(Lookup.CODE),
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE),
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(Lookup.CODE),
				new ExpandedValueType(true, Lookup.CODE)
		));
	}

	@Test
	public void typeNotEqual_singleton() throws Exception {
		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(Lookup.CODE),
				new ExpandedValueType(false, Lookup.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE),
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));

		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(Lookup.CODE),
				Lookup.CODE
		));
		assertEquals(false, typeEquivalent(
				Lookup.CODE,
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.CODE),
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));
	}

	@Test
	public void typeEqual_largerSecondArray() throws Exception {
		//If the length of the second array is >= to the first array, they are considered equal.
		//The reason for this is that the first array specifies the minimum requirements. Anything after the requirements,
		//can safely be ignored.

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER),
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER, Lookup.NUMBER)
		));

		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(Lookup.CODE),
				new ExpandedValueType(false, Lookup.CODE, Lookup.CODE)
		));
	}

	@Test
	public void typeNotEqual_largerFirstArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER, Lookup.NUMBER),
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER),
				new SingletonArrayExpandedValueType(Lookup.CODE)
		));
	}

	@Test
	public void typeEqual_nestedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
				),
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(true, Lookup.CODE, Lookup.NUMBER)
				),
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(true, Lookup.CODE, Lookup.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE,
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
				),
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER),
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
				)
		));
	}

	@Test
	public void typeNotEqual_nestedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(true, Lookup.CODE)
				),
				new ExpandedValueType(false, Lookup.CODE,
						new ExpandedValueType(false, Lookup.CODE, Lookup.NUMBER)
				)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE,
						new ExpandedValueType(true, Lookup.CODE, Lookup.NUMBER)
				),
				new ExpandedValueType(false, Lookup.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, Lookup.CODE,
						new ExpandedValueType(true, Lookup.CODE, Lookup.NUMBER)
				),
				new ExpandedValueType(true, Lookup.CODE)
		));
	}

	@Test
	public void typeEqual_expandedAndLookup() throws Exception {
		assertEquals(true, typeEquivalent(Lookup.ANYTHING, Lookup.ANYTHING.getExpanded()));
		assertEquals(true, typeEquivalent(Lookup.ANYTHING.getExpanded(), Lookup.ANYTHING));
		assertEquals(true, typeEquivalent(Lookup._VARIABLE, Lookup._VARIABLE.getExpanded()));
		assertEquals(true, typeEquivalent(Lookup._VARIABLE.getExpanded(), Lookup._VARIABLE));

		assertEquals(true, typeEquivalent(Lookup.NUMBER.getExpanded(), Lookup.NUMBER));
		assertEquals(true, typeEquivalent(Lookup.NUMBER, Lookup.NUMBER.getExpanded()));
	}

}