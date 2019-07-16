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

import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.TwinValue;
import java.io.InputStream;

public class NullableStringDelta extends TypeCodec {
    private static final long serialVersionUID = 1L;

    public NullableStringDelta() {}

    /**
     * Reads in a stream of data and stores it to a TwinValue object
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a new TwinValue object with the data as its parameters
     */
    public ScalarValue decode(InputStream in) {
        ScalarValue subtractionLength = TypeCodec.NULLABLE_INTEGER.decode(in);
        if (subtractionLength == null)
            return null;
        ScalarValue difference = TypeCodec.ASCII.decode(in);
        return new TwinValue(subtractionLength, difference);
    }

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param value
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue value) {
        if (value.isNull())
            return TypeCodec.NULL_VALUE_ENCODING;
        TwinValue diff = (TwinValue) value;
        byte[] subtractionLength = TypeCodec.NULLABLE_INTEGER.encode(diff.first);
        byte[] difference = TypeCodec.ASCII.encode(diff.second);
        byte[] encoded = new byte[subtractionLength.length + difference.length];
        System.arraycopy(subtractionLength, 0, encoded, 0, subtractionLength.length);
        System.arraycopy(difference, 0, encoded, subtractionLength.length, difference.length);
        return encoded;
    }

    /**
     * 
     * @return Returns a new StringValue object with a defualt value
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

    /**
     * 
     * @param value
     *            Passed value to the new StringValue object
     * @return Returns a new StringValue object with the passed value
     */
    public ScalarValue fromString(String value) {
        return new StringValue(value);
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
