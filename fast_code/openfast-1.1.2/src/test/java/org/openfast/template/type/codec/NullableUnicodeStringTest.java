package org.openfast.template.type.codec;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.test.OpenFastTestCase;

public class NullableUnicodeStringTest extends OpenFastTestCase {
    public void testEncodeDecode() throws Exception {
        assertEncodeDecode(string("Yo"), "10000011 01011001 01101111", TypeCodec.NULLABLE_UNICODE);
        assertEncodeDecode(string("\u00f1"), "10000011 11000011 10110001", TypeCodec.NULLABLE_UNICODE);
        assertEncodeDecode(string("A\u00ea\u00f1\u00fcC"),
                "10001001 01000001 11000011 10101010 11000011 10110001 11000011 10111100 01000011",
                TypeCodec.NULLABLE_UNICODE);
        assertEncodeDecode(null, "10000000", TypeCodec.NULLABLE_UNICODE);
    }

    public void testEncode127ByteString() {
        TypeCodec codec = new NullableUnicodeString();
        byte[] b = new byte[127];
        Arrays.fill(b, (byte)'1');
        String expected = new String(b);
        byte[] bytes = codec.encode(new StringValue(expected));
        final ScalarValue value = codec.decode(new ByteArrayInputStream(bytes));
        String actual = value.toString();
        assertEquals(expected, actual);
    }
}
