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

package org.openfast;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.openfast.error.FastConstants;

public class DecimalValue extends NumericValue {
    private static final long serialVersionUID = 1L;

    public final int exponent;
    public final long mantissa;

    public DecimalValue(double value) {
        if (value == 0.0) {
            this.exponent = 0;
            this.mantissa = 0;

            return;
        }

        BigDecimal decimalValue = BigDecimal.valueOf(value);
        int exponent = decimalValue.scale();
        long mantissa = decimalValue.unscaledValue().longValue();

        while (((mantissa % 10) == 0) && (mantissa != 0)) {
            mantissa /= 10;
            exponent -= 1;
        }

        this.mantissa = mantissa;
        this.exponent = -exponent;
    }

    public DecimalValue(long mantissa, int exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    public DecimalValue(BigDecimal bigDecimal) {
        this.mantissa = bigDecimal.unscaledValue().longValue();
        this.exponent = -1 * bigDecimal.scale();
    }

    @Override
    public NumericValue increment() {
        return null;
    }

    @Override
    public NumericValue decrement() {
        return null;
    }

    @Override
    public Object toObject() {
        return toBigDecimal();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof DecimalValue)) {
            return false;
        }

        return equals((DecimalValue) obj);
    }

    public boolean equals(DecimalValue other) {
        return other.mantissa == this.mantissa && other.exponent == this.exponent;
    }

    @Override
    public NumericValue subtract(NumericValue subtrahend) {
        return new DecimalValue(toBigDecimal().subtract(((DecimalValue)subtrahend).toBigDecimal()));
    }

    @Override
    public NumericValue add(NumericValue addend) {
        return new DecimalValue(toBigDecimal().add(((DecimalValue)addend).toBigDecimal()));
    }

    @Override
    public String serialize() {
        return toString();
    }

    @Override
    public boolean equals(int value) {
        return false;
    }

    @Override
    public long toLong() {
        if (exponent < 0)
            Global.handleError(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, "");
        return (getValue());
    }

    @Override
    public int toInt() {
        long value = getValue();
        if (exponent < 0 || (value) > Integer.MAX_VALUE)
            Global.handleError(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, "");
        return (int) (value);
    }

    @Override
    public short toShort() {
        long value = getValue();
        if (exponent < 0 || (value) > Short.MAX_VALUE)
            Global.handleError(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, "");
        return (short) (value);
    }

    @Override
    public byte toByte() {
        long value = getValue();
        if (exponent < 0 || (value) > Byte.MAX_VALUE)
            Global.handleError(FastConstants.R5_DECIMAL_CANT_CONVERT_TO_INT, "");
        return (byte) (value);
    }

    private long getValue() {
        return mantissa * ((long) Math.pow(10, exponent));
    }

    /**
     * The double value should be rounded using a given precision by users of this method.
     */
    @Override
    public double toDouble() {
        return mantissa * Math.pow(10.0, exponent);
    }

    @Override
    public BigDecimal toBigDecimal() {
        return new BigDecimal(BigInteger.valueOf(mantissa), -exponent);
    }

    @Override
    public String toString() {
//        return "Not Calculated";
        return toBigDecimal().toPlainString();
    }

    @Override
    public int hashCode() {
        return exponent * 37 + (int) mantissa;
    }
}
