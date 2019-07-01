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

public final class NullableUnsignedInteger extends IntegerCodec {
    private static final long serialVersionUID = 1L;

    NullableUnsignedInteger() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param v
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue v) {
        if (v.isNull()) {
            return TypeCodec.NULL_VALUE_ENCODING;
        }
        return TypeCodec.UINT.encodeValue(((NumericValue) v).increment());
    }

    /**
     * Reads in a stream of data and stores it to a NumericValue object
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a NumericValue object
     */
    public ScalarValue decode(InputStream in) {
        NumericValue value = (NumericValue) TypeCodec.UINT.decode(in);
        if (value.equals(0)) {
            return null;
        }
        return value.decrement();
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
