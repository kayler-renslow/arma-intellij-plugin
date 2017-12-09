package com.kaylerrenslow.armaplugin.lang.sqf.syntax;

import com.kaylerrenslow.armaplugin.lang.sqf.syntax.ValueType.BaseType;
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
		assertEquals(true, typeEquivalent(BaseType.CODE, BaseType.CODE));
		assertEquals(true, typeEquivalent(BaseType.CODE, new ExpandedValueType(BaseType.CODE)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.CODE), BaseType.CODE));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.CODE), new ExpandedValueType(BaseType.CODE)));
	}

	@Test
	public void typeEqual_number() throws Exception {
		assertEquals(true, typeEquivalent(BaseType.NUMBER, BaseType.NUMBER));
		assertEquals(true, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.NUMBER)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.NUMBER), BaseType.NUMBER));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.NUMBER), new ExpandedValueType(BaseType.NUMBER)));
	}

	@Test
	public void typeEqual_object() throws Exception {
		assertEquals(true, typeEquivalent(BaseType.OBJECT, BaseType.OBJECT));
		assertEquals(true, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.OBJECT)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.OBJECT), BaseType.OBJECT));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.OBJECT), new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_number() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.CONFIG)));
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.CODE)));
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_object() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.CONFIG)));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.CODE)));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.NUMBER)));
	}

	@Test
	public void typeEqual_array_empty() throws Exception {
		assertEquals(true, typeEquivalent(BaseType.ARRAY, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), BaseType.ARRAY));
	}

	@Test
	public void typeNotEqual_array_empty() throws Exception {
		//these are not equal for sanity reasons. It doesn't really make sense to ever have an Expanded type of ARRAY
		//and would become a real pain in the butt to assume that ARRAY is equal to ExpandedValueType(ARRAY)
		assertEquals(false, typeEquivalent(BaseType.ARRAY, new ExpandedValueType(BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(BaseType.ARRAY), BaseType.ARRAY));
	}

	@Test
	public void typeEqual_emptyUnboundedArray() throws Exception {
		//these are true because an unbounded array with no elements provided counts as an array of infinitely many things or nothing

		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true, BaseType.NUMBER), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true, BaseType.NUMBER)));
	}

	@Test
	public void typeNotEqual_emptyUnboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), BaseType.NUMBER));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), BaseType.OBJECT));
	}

	@Test
	public void typeEquivalence_emptyBoundedExpandedType() throws Exception {
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), BaseType.NUMBER));
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), BaseType.OBJECT));
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(false)));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(false)));

		assertEquals(true, typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(BaseType.ARRAY, new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(false), BaseType.ARRAY));
	}

	@Test
	public void typeEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE),
				new ExpandedValueType(true, BaseType.CODE)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT, BaseType.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE, BaseType.OBJECT, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.CODE, BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT, BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new ExpandedValueType(true, BaseType.NUMBER)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, BaseType.NUMBER), BaseType.NUMBER));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, BaseType.OBJECT), BaseType.OBJECT));
	}

	@Test
	public void typeEqual_notEmpty_array() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_array() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT, BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.OBJECT)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT)
		));
	}

	@Test
	public void typeEqual_singleton() throws Exception {
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(true, BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_singleton() throws Exception {
		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				BaseType.CODE
		));
		assertEquals(false, typeEquivalent(
				BaseType.CODE,
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_largerSecondArray() throws Exception {
		//If the length of the second array is >= to the first array, they are considered equal.
		//The reason for this is that the first array specifies the minimum requirements. Anything after the requirements,
		//can safely be ignored.

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER, BaseType.NUMBER)
		));

		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE, BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_largerFirstArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER, BaseType.NUMBER),
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_nestedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER),
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));
	}

	@Test
	public void typeNotEqual_nestedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE)
				),
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, BaseType.NUMBER)
				)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(true, BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_expandedAndLookup() throws Exception {
		assertEquals(true, typeEquivalent(BaseType.ANYTHING, BaseType.ANYTHING.getExpanded()));
		assertEquals(true, typeEquivalent(BaseType.ANYTHING.getExpanded(), BaseType.ANYTHING));
		assertEquals(true, typeEquivalent(BaseType._VARIABLE, BaseType._VARIABLE.getExpanded()));
		assertEquals(true, typeEquivalent(BaseType._VARIABLE.getExpanded(), BaseType._VARIABLE));

		assertEquals(true, typeEquivalent(BaseType.NUMBER.getExpanded(), BaseType.NUMBER));
		assertEquals(true, typeEquivalent(BaseType.NUMBER, BaseType.NUMBER.getExpanded()));
	}

	@Test
	public void typeEqual_polymorphTypes() throws Exception {
		assertEquals(true, typeEquivalent(BaseType.ANYTHING, new PolymorphicWrapperValueType(BaseType.ANYTHING)));
		assertEquals(true, typeEquivalent(new PolymorphicWrapperValueType(BaseType.ANYTHING), BaseType.ANYTHING));
		assertEquals(true, typeEquivalent(new PolymorphicWrapperValueType(BaseType.NUMBER), BaseType.NUMBER));
		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(BaseType.NUMBER);
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CONFIG));

			assertEquals(true, typeEquivalent(BaseType.ANYTHING, wrap));
			assertEquals(true, typeEquivalent(wrap, BaseType.ANYTHING));
			assertEquals(true, typeEquivalent(BaseType.NUMBER, wrap));
			assertEquals(true, typeEquivalent(wrap, BaseType.NUMBER));
			assertEquals(true, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(true, typeEquivalent(wrap, BaseType.CONFIG));
		}

		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER));
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CONFIG));

			assertEquals(true, typeEquivalent(BaseType.ANYTHING, wrap));
			assertEquals(true, typeEquivalent(wrap, BaseType.ANYTHING));
			assertEquals(true, typeEquivalent(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER), wrap));
			assertEquals(true, typeEquivalent(wrap, new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)));
			assertEquals(true, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(true, typeEquivalent(wrap, BaseType.CONFIG));
		}
	}

	@Test
	public void typeNotEqual_polymorphTypes() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.NUMBER, new PolymorphicWrapperValueType(BaseType.CONTROL)));
		assertEquals(false, typeEquivalent(new PolymorphicWrapperValueType(BaseType.CONTROL), BaseType.NUMBER));
		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(BaseType.CONTROL);
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.DISPLAY));

			assertEquals(false, typeEquivalent(BaseType.NUMBER, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.NUMBER));
			assertEquals(false, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.CONFIG));
		}

		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(new ExpandedValueType(false, BaseType.DISPLAY, BaseType.DISPLAY));
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CODE));

			assertEquals(false, typeEquivalent(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER), wrap));
			assertEquals(false, typeEquivalent(wrap, new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)));
			assertEquals(false, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.CONFIG));
		}
	}

}