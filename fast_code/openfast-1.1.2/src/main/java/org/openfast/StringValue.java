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
import org.openfast.error.FastConstants;

public class StringValue extends ScalarValue {
    private static final long serialVersionUID = 1L;
    public final String value;

    public StringValue(String value) {
        if (value == null)
            throw new NullPointerException();
        this.value = value;
    }
    @Override
    public byte toByte() {
        int value = toInt();
        if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) {
            Global.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value
                    + "\" is too large to fit into a byte.");
            return 0;
        }
        return (byte) value;
    }
    @Override
    public short toShort() {
        int value = toInt();
        if (value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
            Global.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value
                    + "\" is too large to fit into a short.");
            return 0;
        }
        return (short) value;
    }
    @Override
    public int toInt() {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value
                    + "\" is too large to fit into an int.", e);
            return 0;
        }
    }
    @Override
    public long toLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value \"" + value
                    + "\" is too large to fit into a long.", e);
            return 0;
        }
    }
    @Override
    public double toDouble() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.R4_NUMERIC_VALUE_TOO_LARGE, "The value\"" + value
                    + "\" is too large to fit into a double.", e);
            return 0.0;
        }
    }
    @Override
    public Object toObject() {
        return value;
    }
    @Override
    public byte[] getBytes() {
        return value.getBytes();
    }
    @Override
    public BigDecimal toBigDecimal() {
        return new BigDecimal(value);
    }
    @Override
    public String toString() {
        return value;
    }
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof StringValue)) {
            return false;
        }
        return equals((StringValue) obj);
    }
    private boolean equals(StringValue otherValue) {
        return value.equals(otherValue.value);
    }
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    @Override
    public boolean equalsValue(String defaultValue) {
        return value.equals(defaultValue);
    }
}
