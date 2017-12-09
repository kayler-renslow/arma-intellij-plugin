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
		assertEquals(true, typeEquivalent(ValueType.BaseType.CODE, new ExpandedValueType(BaseType.CODE)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(ValueType.BaseType.CODE), ValueType.BaseType.CODE));
		assertEquals(true, typeEquivalent(new ExpandedValueType(ValueType.BaseType.CODE), new ExpandedValueType(ValueType.BaseType.CODE)));
	}

	@Test
	public void typeEqual_number() throws Exception {
		assertEquals(true, typeEquivalent(ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER));
		assertEquals(true, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(ValueType.BaseType.NUMBER)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(ValueType.BaseType.NUMBER), ValueType.BaseType.NUMBER));
		assertEquals(true, typeEquivalent(new ExpandedValueType(ValueType.BaseType.NUMBER), new ExpandedValueType(BaseType.NUMBER)));
	}

	@Test
	public void typeEqual_object() throws Exception {
		assertEquals(true, typeEquivalent(ValueType.BaseType.OBJECT, BaseType.OBJECT));
		assertEquals(true, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(ValueType.BaseType.OBJECT)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(BaseType.OBJECT), ValueType.BaseType.OBJECT));
		assertEquals(true, typeEquivalent(new ExpandedValueType(ValueType.BaseType.OBJECT), new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_number() throws Exception {
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(ValueType.BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(BaseType.CONFIG)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(BaseType.CODE)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(BaseType.OBJECT)));
	}

	@Test
	public void typeNotEqual_object() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.OBJECT, new ExpandedValueType(ValueType.BaseType.CONFIG)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.OBJECT, new ExpandedValueType(BaseType.CODE)));
		assertEquals(false, typeEquivalent(ValueType.BaseType.OBJECT, new ExpandedValueType(ValueType.BaseType.NUMBER)));
	}

	@Test
	public void typeEqual_array_empty() throws Exception {
		assertEquals(true, typeEquivalent(ValueType.BaseType.ARRAY, new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), BaseType.ARRAY));
	}

	@Test
	public void typeNotEqual_array_empty() throws Exception {
		assertEquals(false, typeEquivalent(BaseType.ARRAY, new ExpandedValueType(BaseType.ARRAY)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(BaseType.ARRAY), ValueType.BaseType.ARRAY));
	}

	@Test
	public void typeEqual_emptyUnboundedArray() throws Exception {
		//these are true because an unbounded array with no elements provided counts as an array of infinitely many things or nothing

		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true, ValueType.BaseType.NUMBER), new ExpandedValueType(true)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(true), new ExpandedValueType(true, ValueType.BaseType.NUMBER)));
	}

	@Test
	public void typeNotEqual_emptyUnboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), ValueType.BaseType.NUMBER));
		assertEquals(false, typeEquivalent(ValueType.BaseType.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true), BaseType.OBJECT));
	}

	@Test
	public void typeEquivalence_emptyBoundedExpandedType() throws Exception {
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), ValueType.BaseType.NUMBER));
		assertEquals(false, typeEquivalent(new ExpandedValueType(false), ValueType.BaseType.OBJECT));
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(false)));
		assertEquals(false, typeEquivalent(BaseType.OBJECT, new ExpandedValueType(false)));

		assertEquals(true, typeEquivalent(new ExpandedValueType(false), new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(ValueType.BaseType.ARRAY, new ExpandedValueType(false)));
		assertEquals(true, typeEquivalent(new ExpandedValueType(false), ValueType.BaseType.ARRAY));
	}

	@Test
	public void typeEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.OBJECT)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.CODE),
				new ExpandedValueType(true, ValueType.BaseType.CODE)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, BaseType.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, BaseType.OBJECT, ValueType.BaseType.OBJECT)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.CODE, ValueType.BaseType.OBJECT, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.CODE, ValueType.BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_unboundedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER),
				new ExpandedValueType(true, ValueType.BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.NUMBER),
				new ExpandedValueType(true, BaseType.NUMBER, ValueType.BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.OBJECT, ValueType.BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(ValueType.BaseType.NUMBER, new ExpandedValueType(true, BaseType.NUMBER)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, ValueType.BaseType.NUMBER), ValueType.BaseType.NUMBER));
		assertEquals(false, typeEquivalent(ValueType.BaseType.OBJECT, new ExpandedValueType(true)));
		assertEquals(false, typeEquivalent(new ExpandedValueType(true, BaseType.OBJECT), BaseType.OBJECT));
	}

	@Test
	public void typeEqual_notEmpty_array() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.NUMBER, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER, ValueType.BaseType.OBJECT)
		));
	}

	@Test
	public void typeNotEqual_notEmpty_array() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE, BaseType.NUMBER),
				new ExpandedValueType(true, ValueType.BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, ValueType.BaseType.CODE, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.NUMBER, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.OBJECT, ValueType.BaseType.OBJECT)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.NUMBER),
				new ExpandedValueType(false, ValueType.BaseType.OBJECT)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE),
				new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.CODE),
				new ExpandedValueType(false, ValueType.BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.OBJECT),
				new ExpandedValueType(true, ValueType.BaseType.OBJECT)
		));
	}

	@Test
	public void typeEqual_singleton() throws Exception {
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE),
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE)
		));
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.CODE),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE),
				new ExpandedValueType(true, ValueType.BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_singleton() throws Exception {
		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE),
				new ExpandedValueType(false, BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE),
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE),
				ValueType.BaseType.CODE
		));
		assertEquals(false, typeEquivalent(
				ValueType.BaseType.CODE,
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE, BaseType.CODE),
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_largerSecondArray() throws Exception {
		//If the length of the second array is >= to the first array, they are considered equal.
		//The reason for this is that the first array specifies the minimum requirements. Anything after the requirements,
		//can safely be ignored.

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, ValueType.BaseType.NUMBER),
				new ExpandedValueType(false, ValueType.BaseType.CODE, BaseType.NUMBER, ValueType.BaseType.NUMBER)
		));

		assertEquals(true, typeEquivalent(
				new SingletonArrayExpandedValueType(ValueType.BaseType.CODE),
				new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.CODE)
		));
	}

	@Test
	public void typeNotEqual_largerFirstArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE, ValueType.BaseType.NUMBER, ValueType.BaseType.NUMBER),
				new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER)
		));
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER),
				new SingletonArrayExpandedValueType(BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_nestedArray() throws Exception {
		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, ValueType.BaseType.NUMBER)
				),
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(true, ValueType.BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(true, BaseType.CODE, ValueType.BaseType.NUMBER)
				)
		));

		assertEquals(true, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.CODE,
						new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER)
				),
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER),
						new ExpandedValueType(false, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER)
				)
		));
	}

	@Test
	public void typeNotEqual_nestedArray() throws Exception {
		assertEquals(false, typeEquivalent(
				new ExpandedValueType(false, BaseType.CODE,
						new ExpandedValueType(true, ValueType.BaseType.CODE)
				),
				new ExpandedValueType(false, ValueType.BaseType.CODE,
						new ExpandedValueType(false, BaseType.CODE, ValueType.BaseType.NUMBER)
				)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, ValueType.BaseType.CODE,
						new ExpandedValueType(true, ValueType.BaseType.CODE, ValueType.BaseType.NUMBER)
				),
				new ExpandedValueType(false, BaseType.CODE)
		));

		assertEquals(false, typeEquivalent(
				new ExpandedValueType(true, BaseType.CODE,
						new ExpandedValueType(true, ValueType.BaseType.CODE, BaseType.NUMBER)
				),
				new ExpandedValueType(true, ValueType.BaseType.CODE)
		));
	}

	@Test
	public void typeEqual_expandedAndLookup() throws Exception {
		assertEquals(true, typeEquivalent(ValueType.BaseType.ANYTHING, BaseType.ANYTHING.getExpanded()));
		assertEquals(true, typeEquivalent(ValueType.BaseType.ANYTHING.getExpanded(), ValueType.BaseType.ANYTHING));
		assertEquals(true, typeEquivalent(ValueType.BaseType._VARIABLE, ValueType.BaseType._VARIABLE.getExpanded()));
		assertEquals(true, typeEquivalent(BaseType._VARIABLE.getExpanded(), BaseType._VARIABLE));

		assertEquals(true, typeEquivalent(ValueType.BaseType.NUMBER.getExpanded(), BaseType.NUMBER));
		assertEquals(true, typeEquivalent(ValueType.BaseType.NUMBER, BaseType.NUMBER.getExpanded()));
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

			assertEquals(false, typeEquivalent(BaseType.ANYTHING, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.ANYTHING));
			assertEquals(false, typeEquivalent(BaseType.NUMBER, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.NUMBER));
			assertEquals(false, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.CONFIG));
		}

		{
			PolymorphicWrapperValueType wrap = new PolymorphicWrapperValueType(new ExpandedValueType(false, BaseType.DISPLAY, BaseType.DISPLAY));
			wrap.getPolymorphicTypes().add(new PolymorphicWrapperValueType(BaseType.CODE));

			assertEquals(false, typeEquivalent(BaseType.ANYTHING, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.ANYTHING));
			assertEquals(false, typeEquivalent(new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER), wrap));
			assertEquals(false, typeEquivalent(wrap, new ExpandedValueType(false, BaseType.NUMBER, BaseType.NUMBER)));
			assertEquals(false, typeEquivalent(BaseType.CONFIG, wrap));
			assertEquals(false, typeEquivalent(wrap, BaseType.CONFIG));
		}
	}

}