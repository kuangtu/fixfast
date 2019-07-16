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

import java.io.IOException;
import java.io.InputStream;

import org.openfast.Global;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;

public final class SignedInteger extends IntegerCodec {
    private static final long serialVersionUID = 1L;

    SignedInteger() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param value
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue value) {
        long longValue = ((NumericValue) value).toLong();
        int size = getSignedIntegerSize(longValue);
        byte[] encoding = new byte[size];
        for (int factor = 0; factor < size; factor++) {
            int bitMask = (factor == (size - 1)) ? 0x3f : 0x7f;
            encoding[size - factor - 1] = (byte) ((longValue >> (factor * 7)) & bitMask);
        }
        // Get the sign bit from the long value and set it on the first byte
        // 01000000 00000000 ... 00000000
        // ^----SIGN BIT
        encoding[0] |= (0x40 & (longValue >> 57));
        return encoding;
    }

    /**
     * 
     * @param in
     *            The InputStream to be decoded
     * @return the decoded value from the fast input stream
     */
    public ScalarValue decode(InputStream in) {
        long value = 0;
        try {
            int byt = in.read();
            if (byt < 0) {
                Global.handleError(FastConstants.END_OF_STREAM, "The end of the input stream has been reached.");
                return null; // short circuit if global error handler does not throw exception
            }
            if ((byt & 0x40) > 0) {
                value = -1;
            }
            value = (value << 7) | (byt & 0x7f);
            while ((byt & 0x80) == 0) {
                byt = in.read();
                if (byt < 0) {
                    Global.handleError(FastConstants.END_OF_STREAM, "The end of the input stream has been reached.");
                    return null; // short circuit if global error handler does not throw exception
                }
                value = (value << 7) | (byt & 0x7f);
            }
        } catch (IOException e) {
            Global.handleError(FastConstants.IO_ERROR, "A IO error has been encountered while decoding.", e);
            return null; // short circuit if global error handler does not throw exception
        }
        return createValue(value);
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
