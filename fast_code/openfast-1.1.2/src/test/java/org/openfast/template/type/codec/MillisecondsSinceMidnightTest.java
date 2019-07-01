package org.openfast.template.type.codec;

import org.openfast.DateValue;
import org.openfast.test.OpenFastTestCase;

public class MillisecondsSinceMidnightTest extends OpenFastTestCase {
    public void testEncodeDecode() {
        assertEncodeDecode(new DateValue(time(5, 30, 25, 254)), "00001001 00111010 00000100 11100110", TypeCodec.TIME_IN_MS);
    }
}
