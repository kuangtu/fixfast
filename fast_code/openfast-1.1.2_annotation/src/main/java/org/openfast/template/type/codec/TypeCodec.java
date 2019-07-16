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
package org.openfast.template.type.codec;

import java.io.InputStream;
import java.io.Serializable;
import org.openfast.ScalarValue;

public abstract class TypeCodec implements Serializable {
    private static final long serialVersionUID = 1L;
    protected static final byte STOP_BIT = (byte) 0x80;
    static final byte[] NULL_VALUE_ENCODING = new byte[] { STOP_BIT };
    // Codec Definitions
    public static final TypeCodec UINT = new UnsignedInteger();
    public static final TypeCodec INTEGER = new SignedInteger();
    public static final TypeCodec ASCII = new AsciiString();
    public static final TypeCodec UNICODE = new UnicodeString();
    public static final TypeCodec BIT_VECTOR = new BitVectorType();
    public static final TypeCodec BYTE_VECTOR = new ByteVectorType();
    public static final TypeCodec SF_SCALED_NUMBER = new SingleFieldDecimal();
    public static final TypeCodec STRING_DELTA = new StringDelta();
    public static final TypeCodec NULLABLE_UNSIGNED_INTEGER = new NullableUnsignedInteger();
    public static final TypeCodec NULLABLE_INTEGER = new NullableSignedInteger();
    public static final TypeCodec NULLABLE_ASCII = new NullableAsciiString();
    public static final TypeCodec NULLABLE_UNICODE = new NullableUnicodeString();
    public static final TypeCodec NULLABLE_BYTE_VECTOR_TYPE = new NullableByteVector();
    public static final TypeCodec NULLABLE_SF_SCALED_NUMBER = new NullableSingleFieldDecimal();
    public static final TypeCodec NULLABLE_STRING_DELTA = new NullableStringDelta();
    // DATE CODECS
    public static final TypeCodec DATE_STRING = new DateString("yyyyMMdd");
    public static final TypeCodec DATE_INTEGER = new DateInteger();
    public static final TypeCodec TIMESTAMP_STRING = new DateString("yyyyMMddhhmmssSSS");
    public static final TypeCodec TIMESTAMP_INTEGER = new TimestampInteger();
    public static final TypeCodec EPOCH_TIMESTAMP = new EpochTimestamp();
    public static final TypeCodec TIME_STRING = new DateString("hhmmssSSS");
    public static final TypeCodec TIME_INTEGER = new TimeInteger();
    public static final TypeCodec TIME_IN_MS = new MillisecondsSinceMidnight();

    public abstract byte[] encodeValue(ScalarValue value);

    public abstract ScalarValue decode(InputStream in);

    /**
     * Template Method to encode the passed object, the actual encoding is done
     * in the encodeValue() method overridden in sub-classes.
     * <p>
     * <b>Note</b>: The final SBIT is set in this method, not in encodeValue().
     * </p>
     * 
     * @param value
     *            The ScalarValue object to be encoded
     * @return Returns an encoded byte array with an added stop bit at the end
     */
    public byte[] encode(ScalarValue value) {
        byte[] encoding = encodeValue(value);
        encoding[encoding.length - 1] |= STOP_BIT;
        return encoding;
    }

    /**
     * 
     * @return Returns false
     */
    public boolean isNullable() {
        return false;
    }
}
