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
import org.openfast.DecimalValue;
import org.openfast.Global;
import org.openfast.IntegerValue;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.LongValue;

final class NullableSingleFieldDecimal extends TypeCodec {
    private static final long serialVersionUID = 1L;

    NullableSingleFieldDecimal() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param v
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue v) {
        if (v == ScalarValue.NULL) {
            return TypeCodec.NULL_VALUE_ENCODING;
        }
        ByteArrayOutputStream buffer = Global.getBuffer();
        DecimalValue value = (DecimalValue) v;
        try {
            if (Math.abs(value.exponent) > 63) {
                Global.handleError(FastConstants.R1_LARGE_DECIMAL, "");
            }
            buffer.write(TypeCodec.NULLABLE_INTEGER.encode(new IntegerValue(value.exponent)));
            buffer.write(TypeCodec.INTEGER.encode(new LongValue(value.mantissa)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer.toByteArray();
    }

    /**
     * Reads in a stream of data and stores it to a decimalValue object
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a decimalValue object with the data stream
     */
    public ScalarValue decode(InputStream in) {
        ScalarValue exp = TypeCodec.NULLABLE_INTEGER.decode(in);
        if ((exp == null) || exp.isNull()) {
            return null;
        }
        int exponent = ((NumericValue) exp).toInt();
        long mantissa = ((NumericValue) TypeCodec.INTEGER.decode(in)).toLong();
        DecimalValue decimalValue = new DecimalValue(mantissa, exponent);
        return decimalValue;
    }

    /**
     * Convert a string to a DecimalValue object with the string as the passed
     * value
     * 
     * @param value
     *            The value as a string to be converted
     * @return Returns a new DecimalValue object
     */
    public ScalarValue fromString(String value) {
        return new DecimalValue(Double.parseDouble(value));
    }

    /**
     * 
     * @return Returns a new DecimalValue object with a default value
     */
    public ScalarValue getDefaultValue() {
        return new DecimalValue(0.0);
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
