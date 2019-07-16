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

import java.io.InputStream;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;

public final class NullableSignedInteger extends IntegerCodec {
    private static final long serialVersionUID = 1L;

    NullableSignedInteger() {}

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
        NumericValue intValue = (NumericValue) value;
        if (intValue.toLong() >= 0) {
            return TypeCodec.INTEGER.encodeValue(intValue.increment());
        } else {
            return TypeCodec.INTEGER.encodeValue(intValue);
        }
    }

    /**
     * Reads in a stream of data and stores it to a numericValue object - type
     * integer
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a new numericValue object
     */
    public ScalarValue decode(InputStream in) {
        NumericValue numericValue = ((NumericValue) TypeCodec.INTEGER.decode(in));
        long value = numericValue.toLong();
        if (value == 0) {
            return null;
        }
        if (value > 0) {
            return numericValue.decrement();
        }
        return numericValue;
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
