package org.openfast;

import java.math.BigDecimal;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.test.OpenFastTestCase;

public class IntegerValueTest extends OpenFastTestCase {
    public void testAddOverflowToLong() {
        IntegerValue maxValue = new IntegerValue(Integer.MAX_VALUE);
        assertEquals(2147483648L, maxValue.add(new IntegerValue(1)).toLong());
    }
    
    public void testIncrementOverflowToLong() {
        IntegerValue maxValue = new IntegerValue(Integer.MAX_VALUE);
        assertEquals(2147483648L, maxValue.increment().toLong());
    }
    
    public void testDecrementOverflowToLong() {
        IntegerValue minValue = new IntegerValue(Integer.MIN_VALUE);
        assertEquals(-2147483649L, minValue.decrement().toLong());
        
    }

    public void testToInt() {
        assertEquals(125, i(125).toInt());
    }

    public void testToLong() {
        assertEquals(125L, i(125).toLong());
    }

    public void testToString() {
        assertEquals("105", i(105).toString());
    }

    public void testToByte() {
        assertEquals(0x7f, i(127).toByte());
    }

    public void testToByteWithLargeInt() {
        try {
            i(128).toByte();
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
        }
    }

    public void testToShort() {
        assertEquals((short) 32767, i(32767).toShort());
    }

    public void testToShortWithLargeInt() {
        try {
            i(32768).toByte();
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, e.getCode());
        }
    }

    public void testToDouble() {
        assertEquals(125.0, i(125).toDouble(), 0.1);
    }

    public void testToBigDecimal() {
        assertEquals(new BigDecimal("125"), i(125).toBigDecimal());
    }
}
