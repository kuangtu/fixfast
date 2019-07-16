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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openfast.ByteUtil;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;

final class AsciiString extends TypeCodec {
    private static final long serialVersionUID = 1L;
    private static final String ZERO_TERMINATOR = "\u0000";
    private static final byte[] ZERO_PREAMBLE = new byte[] { 0, 0 };

    AsciiString() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param value
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue value) {
        if ((value == null) || value.isNull()) {
            throw new IllegalStateException("Only nullable strings can represent null values.");
        }
        String string = value.toString();
        if ((string != null) && (string.length() == 0)) {
            return TypeCodec.NULL_VALUE_ENCODING;
        }
        if (string.startsWith(ZERO_TERMINATOR)) {
            return ZERO_PREAMBLE;
        }
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
            if (bytes.length > 1 && bytes[1] == 0)
                return new StringValue("\u0000");
            return new StringValue("");
        }
        return new StringValue(new String(bytes));
    }

    /**
     * @return Returns a new StringValue object with the passed value
     */
    public ScalarValue fromString(String value) {
        return new StringValue(value);
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
