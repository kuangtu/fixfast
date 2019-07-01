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
package org.openfast.template.type;

import org.openfast.Global;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.LongValue;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.util.Util;

public abstract class IntegerType extends SimpleType {
    private static final long serialVersionUID = 1L;
    protected final long minValue;
    protected final long maxValue;

    public IntegerType(String typeName, long minValue, long maxValue, TypeCodec codec, TypeCodec nullableCodec) {
        super(typeName, codec, nullableCodec);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    /**
     * @param value
     * @return either longvalue or integervalue depending on size of parsed
     *         number
     */
    protected ScalarValue getVal(String value) {
        long longValue;
        try {
            longValue = Long.parseLong(value);
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.S3_INITIAL_VALUE_INCOMP, "The value \"" + value + "\" is not compatable with type "
                    + this);
            return null;
        }
        if (Util.isBiggerThanInt(longValue)) {
            return new LongValue(longValue);
        }
        return new IntegerValue((int) longValue);
    }
    /**
     * @return Returns a default value
     */
    public ScalarValue getDefaultValue() {
        return new IntegerValue(0);
    }
    /**
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the passed value is an instance of an integer or
     *         long
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof IntegerValue || previousValue instanceof LongValue;
    }
    /**
     * Validates the passed ScalarValue, if fails, throws error.
     * 
     * @param value
     *            The ScalarValue object to be validated
     * 
     */
    public void validateValue(ScalarValue value) {
        if (value == null || value.isUndefined())
            return;
        if (value.toLong() > maxValue || value.toLong() < minValue) {
            Global.handleError(FastConstants.D2_INT_OUT_OF_RANGE, "The value " + value + " is out of range for type " + this);
        }
    }
}