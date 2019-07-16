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
package org.openfast.template.operator;

import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class DefaultOperatorCodec extends OperatorCodec {
    private static final long serialVersionUID = 1L;

    DefaultOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }

    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
    	//字段值为null
        if (value == null) {
        	//如果字段初始值未定义,返回null表示不存在
            if (field.getDefaultValue().isUndefined())
                return null;
            //字段有初始值，传输值为NULL
            return ScalarValue.NULL;
        }
        
        //如果字段值和初始值相等,返回null，不需要进行压缩,
        //否则传输值等于字段值
        return value.equals(field.getDefaultValue()) ? null : value;
    }

    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
    	//数据流中的值即为字段的值
        return newValue;
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
    	//数据流中没有传输值,如果初始值未定义,则字段值不存在
        if (field.getDefaultValue().isUndefined())
            return null;
        //字段值为初始值
        return field.getDefaultValue();
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
