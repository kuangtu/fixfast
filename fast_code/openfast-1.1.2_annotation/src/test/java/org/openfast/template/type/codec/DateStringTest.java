package org.openfast.template.type.codec;

import org.openfast.DateValue;
import org.openfast.test.OpenFastTestCase;

public class DateStringTest extends OpenFastTestCase {
    public void testEncodeDecode() {
        assertEncodeDecode(new DateValue(date(2007, 7, 7)), "00110010 00110000 00110000 00110111 00110000 00111000 00110000 10110111",
                TypeCodec.DATE_STRING);
    }
}