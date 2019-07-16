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
package org.openfast.template.operator;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;
import org.openfast.util.Util;

final class DeltaStringOperatorCodec extends AlwaysPresentOperatorCodec {
    private static final long serialVersionUID = 1L;

    DeltaStringOperatorCodec() {
        super(Operator.DELTA, new Type[] { Type.ASCII, Type.STRING, Type.UNICODE, Type.BYTE_VECTOR });
    }

    /**
     * @param value
     * @param priorValue
     * @param field
     * @return
     */
    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
        if (value == null) {
            return ScalarValue.NULL;
        }
        if (priorValue == null) {
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " must have a priorValue defined.");
            return null;
        }
        ScalarValue base = (priorValue.isUndefined()) ? field.getBaseValue() : priorValue;
        return Util.getDifference(value.getBytes(), base.getBytes());
    }

    /**
     * 
     * @param newValue
     * @param previousValue
     * @param field
     * @return Returns null if the passed ScalarValue objects are null,
     *         otherwise
     */
    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
        if ((newValue == null) || newValue.isNull()) {
            return null;
        }
        TwinValue diffValue = (TwinValue) newValue;
        ScalarValue base = (previousValue.isUndefined()) ? field.getBaseValue() : previousValue;
        if (diffValue.first.toInt() > base.toString().length()) {
            Global.handleError(FastConstants.D7_SUBTRCTN_LEN_LONG, "The string diff <" + diffValue
                    + "> cannot be applied to the base value \"" + base + "\" because the subtraction length is too long.");
        }
        byte[] bytes = Util.applyDifference(base, diffValue);
        return field.getType().getValue(bytes);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        throw new IllegalStateException("As of FAST v1.1 Delta values must be present in stream");
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
