package org.openfast.template.type.codec;

import java.util.Calendar;
import java.util.TimeZone;
import org.openfast.DateValue;
import org.openfast.test.OpenFastTestCase;

public class EpochTimestampTest extends OpenFastTestCase {
    public void testEncodeDecode() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(2007, 7, 7, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEncodeDecode(new DateValue(cal.getTime()), "00100010 01000100 00000001 01001000 00111100 10000000",
                TypeCodec.EPOCH_TIMESTAMP);
    }
}
