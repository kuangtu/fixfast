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

import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class IncrementIntegerOperatorCodec extends OperatorCodec {
    private static final long serialVersionUID = 1L;

    IncrementIntegerOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }
    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
    	//如果前值为null,则字段为需要压缩的值
        if (priorValue == null) {
            return value;
        }
      //如果字段值为null
        if (value == null) {
        	//字段可选类型，前值未定义,且字段的初始值未定义,返回null，不需要进行传输
            if (field.isOptional()) {
                if (priorValue == ScalarValue.UNDEFINED && field.getDefaultValue().isUndefined()) {
                    return null;
                }
                //通过NULL表示字段值不存在
                return ScalarValue.NULL;
            } else {
                throw new IllegalArgumentException();
            }
        }
        //字段值非null,前值未定义,且与初始值相同,返回null，不需要进行传输
        if (priorValue.isUndefined()) {
            if (value.equals(field.getDefaultValue())) {
                return null;
            } else {
            	//前值非null,与初始值也不相同,需要压缩字段值
                return value;
            }
        }
        //如果字段值不等于前值加1,需要压缩字段值
        if (!value.equals(((NumericValue) priorValue).increment())) {
            return value;
        }
        //字段值为前值加1,则不用压缩处理,返回null
        return null;
    }
    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
    	//字段值出现在数据流中,则为字段值
        return newValue;
    }
    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
    	//如果前值为null,则字段值为null
        if (previousValue == null)
            return null;
        //如果前值未定义,且初始值为未定义,字段可选类型，字段值为null
        if (previousValue.isUndefined()) {
            if (field.getDefaultValue().isUndefined()) {
                if (field.isOptional()) {
                    return null;
                } else {
                    throw new IllegalStateException("Field with operator increment must send a value if no previous value existed.");
                }
            } else {
            	//前值未定义,字段有初始值,字段值为初始值
                return field.getDefaultValue();
            }
        }
        //前值不为空且不是未定义,字段值为前值加1
        return ((NumericValue) previousValue).increment();
    }
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}