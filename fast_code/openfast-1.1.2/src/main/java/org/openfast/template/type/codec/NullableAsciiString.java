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
/**
 *
 */
package org.openfast.template.type.codec;

import org.openfast.ByteUtil;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class NullableAsciiString extends TypeCodec {
    private static final long serialVersionUID = 1L;
    private static final byte[] NULLABLE_EMPTY_STRING = new byte[] { 0x00, 0x00 };
    private static final byte[] ZERO_ENCODING = new byte[] { 0x00, 0x00, 0x00 };

    NullableAsciiString() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param value
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue value) {
        if (value.isNull()) {
            return TypeCodec.NULL_VALUE_ENCODING;
        }
        String string = ((StringValue) value).value;
        if ((string != null) && (string.length() == 0)) {
            return NULLABLE_EMPTY_STRING;
        }
        if (string.startsWith("\u0000"))
            return ZERO_ENCODING;
        return string.getBytes();
    }

    /**
     * Reads in a stream of data and stores it to a StringValue object
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a new StringValue object with the data stream as a String
     */
    public ScalarValue decode(InputStream in) {
        int byt;
        ByteArrayOutputStream buffer = Global.getBuffer();
        try {
            do {
                byt = in.read();
                if (byt < 0) {
                    Global.handleError(FastConstants.END_OF_STREAM, "The end of the input stream has been reached.");
                    return null; // short circuit if global error handler does not throw exception
                }
                buffer.write(byt);
            } while ((byt & 0x80) == 0);
        } catch (IOException e) {
            Global.handleError(FastConstants.IO_ERROR, "A IO error has been encountered while decoding.", e);
            return null; // short circuit if global error handler does not throw exception
        }
        byte[] bytes = buffer.toByteArray();
        bytes[bytes.length - 1] &= 0x7f;
        if (bytes[0] == 0) {
            if (!ByteUtil.isEmpty(bytes))
                Global.handleError(FastConstants.R9_STRING_OVERLONG, null);
            if ((bytes.length == 1)) {
                return null;
            } else if (bytes.length == 2 && bytes[1] == 0) {
                return new StringValue("");
            } else if (bytes.length == 3 && bytes[2] == 0) {
                return new StringValue("\u0000");
            }
        }
        return new StringValue(new String(bytes));
    }

    /**
     * Creates a new StringValue object
     * 
     * @param value
     *            The string to be value of the new object
     * @return Returns a new StringValue object with the passed string as its
     *         parameter
     */
    public ScalarValue fromString(String value) {
        return new StringValue(value);
    }

    /**
     * 
     * @return Returns an empty StringValue object
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

    /**
     * @return Returns true
     */
    public boolean isNullable() {
        return true;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
