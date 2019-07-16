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

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class TailOperatorCodec extends OperatorCodec {
    private static final long serialVersionUID = 1L;

    TailOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }

    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
    	//字段值为null，前值为null，传输值为null
        if (value == null) {
            if (priorValue == null)
                return null;
            //前值未定义且字段初始值未定义，传输值为null
            if (priorValue.isUndefined() && field.getDefaultValue().isUndefined())
                return null;
            //前值已指定，传输值通过NULL表示
            return ScalarValue.NULL;
        }
        //字段值不为null，前值为null，传输值为字段值
        if (priorValue == null) {
            return value;
        }
        //如果前值未定义，基值为初始值或者数据类型的缺省基值
        if (priorValue.isUndefined()) {
            priorValue = field.getBaseValue();
        }
        //转为字节后计算尾部差异
        int index = 0;
        byte[] val = value.getBytes();
        byte[] prior = priorValue.getBytes();
        if (val.length > prior.length)
            return value;
        if (val.length < prior.length)
            Global.handleError(FastConstants.D3_CANT_ENCODE_VALUE, "The value " + val
                    + " cannot be encoded by a tail operator with previous value " + priorValue);
        while (index < val.length && val[index] == prior[index])
            index++;
        if (val.length == index)
            return null;
        return (ScalarValue) field.getType().getValue(val, index, val.length - index);
    }

    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
        StringValue base;
        //字段强制类型，前值为null，D6异常
        if ((previousValue == null) && !field.isOptional()) {
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "");
            return null;
        //前值为null或者未定义，基值为初始值或者数据类型的缺省基值
        } else if ((previousValue == null) || previousValue.isUndefined()) {
            base = (StringValue) field.getBaseValue();
        } else {
        	//基值为前值
            base = (StringValue) previousValue;
        }
        //传输值为null，可选类型字段值为null
        if ((newValue == null) || newValue.isNull()) {
            if (field.isOptional()) {
                return null;
            } else {
                throw new IllegalArgumentException("");
            }
        }
        //基于尾部计算得到字段值
        String delta = ((StringValue) newValue).value;
        int length = Math.max(base.value.length() - delta.length(), 0);
        String root = base.value.substring(0, length);
        return new StringValue(root + delta);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        ScalarValue value = previousValue;
        //前值未定义，字段值为初始值，如果初始值为未定义，字段值为null
        if (value != null && value.isUndefined())
            value = (field.getDefaultValue().isUndefined()) ? null : field.getDefaultValue();
        if (value == null && !field.isOptional())
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " was not present.");
        //字段值为前值
        return value;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}