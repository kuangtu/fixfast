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

import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedValueConverter;
import org.openfast.template.LongValue;

public class DecimalConverter implements ComposedValueConverter {
    private static final long serialVersionUID = 1L;
    private static final FieldValue[] NULL_SET = new FieldValue[] { null, null };
    private static final FieldValue[] UNDEFINED_SET = new FieldValue[] { ScalarValue.UNDEFINED, ScalarValue.UNDEFINED };

    public FieldValue[] split(FieldValue value) {
        if (value == null)
            return NULL_SET;
        else if (value == ScalarValue.UNDEFINED)
            return UNDEFINED_SET;
        DecimalValue decimal = (DecimalValue) value;
        return new FieldValue[] { new IntegerValue(decimal.exponent), new LongValue(decimal.mantissa) };
    }

    public FieldValue compose(FieldValue[] values) {
        if (values[0] == null)
            return null;
        if (values[0] == ScalarValue.UNDEFINED)
            return ScalarValue.UNDEFINED;
        return new DecimalValue(((ScalarValue) values[1]).toLong(), ((ScalarValue) values[0]).toInt());
    }
}
