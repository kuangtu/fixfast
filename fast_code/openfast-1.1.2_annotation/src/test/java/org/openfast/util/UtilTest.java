/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast.util;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;
import org.openfast.test.OpenFastTestCase;

public class UtilTest extends OpenFastTestCase {
    public void testCollectionToString() {
        Map map = new LinkedHashMap();
        map.put("abc", "123");
        map.put("def", "456");
        assertEquals("{abc,def}", Util.collectionToString(map.keySet()));
    }

    public void testByteVector() {
        byte[] bytes = ByteUtil.convertBitStringToFastByteArray("11000000 10000001 10000000");
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        buffer.flip();
        putBytes(bytes, buffer);
        assertEquals(192, (buffer.get() & 0xFF));
        assertEquals(129, (buffer.get() & 0xFF));
        assertEquals(128, (buffer.get() & 0xFF));
        assertFalse(buffer.hasRemaining());
        bytes = ByteUtil.convertBitStringToFastByteArray("10000001 10000001 10000001");
        putBytes(bytes, buffer);
        assertEquals(bytes[0], buffer.get());
        assertEquals(bytes[1], buffer.get());
        assertEquals(bytes[2], buffer.get());
        assertFalse(buffer.hasRemaining());

    }

    private void putBytes(byte[] bytes, ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            buffer.clear();
            buffer.put(bytes);
            buffer.flip();
        }
    }
    public void testGetDifference() {
        assertEquals(tv(0, "d"), diff("abc", "abcd"));
        assertEquals(tv(1, "ed"), diff("abc", "abed"));
        assertEquals(tv(0, "GEH6"), diff("", "GEH6"));
        assertEquals(tv(2, "M6"), diff("GEH6", "GEM6"));
        assertEquals(tv(-3, "ES"), diff("GEM6", "ESM6"));
        assertEquals(tv(-1, "RS"), diff("ESM6", "RSESM6"));
        assertEquals(tv(0, ""), diff("RSESM6", "RSESM6"));
    }
    public void testApplyDifference() {
        assertEquals(b("abcd"), apply("abc", tv(0, "d")));
        assertEquals(b("abed"), apply("abc", tv(1, "ed")));
        assertEquals(b("GEH6"), apply("", tv(0, "GEH6")));
        assertEquals(b("GEM6"), apply("GEH6", tv(2, "M6")));
        assertEquals(b("ESM6"), apply("GEM6", tv(-3, "ES")));
        assertEquals(b("RSESM6"), apply("ESM6", tv(-1, "RS")));
        assertEquals(b("RSESM6"), apply("RSESM6", tv(0, "")));
    }
    private String b(String string) {
        return ByteUtil.convertByteArrayToBitString(string.getBytes());
    }

    public void testIntToTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(2007, 0, 10, 14, 25, 12);
        cal.set(Calendar.MILLISECOND, 253);
        assertEquals(cal.getTime(), Util.toTimestamp(20070110142512253L));
    }
    private byte[] apply(String base, TwinValue diff) {
        return Util.applyDifference(new StringValue(base), diff);
    }
    private TwinValue tv(int sub, String diff) {
        return new TwinValue(new IntegerValue(sub), new ByteVectorValue(diff.getBytes()));
    }
    private ScalarValue diff(String base, String value) {
        return Util.getDifference(value.getBytes(), base.getBytes());
    }
}
