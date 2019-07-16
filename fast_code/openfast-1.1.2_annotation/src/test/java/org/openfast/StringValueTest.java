package org.openfast;

import java.math.BigDecimal;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.OpenFastTestCase;

public class StringValueTest extends OpenFastTestCase {

	public void testToByte() {
		ScalarValue value = string("5");
		assertEquals(0x05, value.toByte());
	}
	
	public void testToByteWithLargeValue() {
		try {
			ScalarValue value = string("128");
			value.toByte();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
		}
	}

	public void testToShort() {
		ScalarValue value = string("128");
		assertEquals((short) 0x80, value.toShort());
	}
	
	public void testToShortWithLargeValue() {
		try {
			ScalarValue value = string("32768");
			value.toShort();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
		}
	}

	public void testToInt() {
		ScalarValue value = string("32768");
		assertEquals(32768, value.toInt());
	}
	
	public void testToIntWithLargeValue() {
		try {
			ScalarValue value = string("2147483648");
			value.toInt();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
		}
	}

	public void testToLong() {
		ScalarValue value = string("2147483648");
		assertEquals(2147483648L, value.toLong());
	}
	
	public void testToLongWithLargeValue() {
		try {
			ScalarValue value = string(" 9223372036854775808");
			value.toLong();
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
		}
	}

	public void testGetBytes() {
		assertEquals("01100001 01100010 01100011 01100100", string("abcd").getBytes());
		assertEquals("01000001 01000010 01000011 01000100", string("ABCD").getBytes());
	}

	public void testToDouble() {
		ScalarValue value = string("  -1.234 ");
		assertEquals(-1.234, value.toDouble(), .001);
	}
	
	public void testToBigDecimal() {
		assertEquals(new BigDecimal("-1.234"), string("-1.234").toBigDecimal());
	}

	public void testToString() {
		ScalarValue value = string("1234abcd");
		assertEquals("1234abcd", value.toString());
	}

}
