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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

public class Operator implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //存放操作符的HashMap，根据Name进行查找
    private static final Map OPERATOR_NAME_MAP = new HashMap();
    
    //操作符名称
    private final String name;

    public static final Operator NONE = new Operator("none") {
        private static final long serialVersionUID = 2L;
        
        //不需要使用字典,字段值一定出现在压缩数据流中
        public boolean usesDictionary() {
            return false;
        }
      //该值不需要进行存放
        public boolean shouldStoreValue(ScalarValue value) {
            return false;
        }
    };
    
    public static final Operator CONSTANT = new Operator("constant") {
        private static final long serialVersionUID = 1L;
        
        //如果该字段的初始值未定义,则报错
        public void validate(Scalar scalar) {
            if (scalar.getDefaultValue().isUndefined())
                Global.handleError(FastConstants.S4_NO_INITIAL_VALUE_FOR_CONST, "The field " + scalar
                        + " must have a default value defined.");
        }

        //该值不需要进行存放
        public boolean shouldStoreValue(ScalarValue value) {
            return false;
        }
        
        //不需要使用字典 
        public boolean usesDictionary() {
            return false;
        }
    };

    public static final Operator DEFAULT = new Operator("default") {
        private static final long serialVersionUID = 1L;
        //如果字段存在属性不是可选类型，且初始值未定义，则报错
        public void validate(Scalar scalar) {
            if (!scalar.isOptional() && scalar.getDefaultValue().isUndefined())
                Global.handleError(FastConstants.S5_NO_INITVAL_MNDTRY_DFALT, "The field " + scalar
                        + " must have a default value defined.");
        }
        
        //如果字段值存在，则进行保存
        public boolean shouldStoreValue(ScalarValue value) {
            return value != null;
        }
    };

    public static final Operator COPY = new Operator("copy") {
        private static final long serialVersionUID = 1L;
        
        //返回对应操作符的编解码操作
        public OperatorCodec getCodec(Type type) {
            return OperatorCodec.COPY_ALL;
        }
    };

    public static final Operator INCREMENT = new Operator("increment");

    public static final Operator DELTA = new Operator("delta") {
        private static final long serialVersionUID = 1L;
        //如果字段存在,则进行保存
        public boolean shouldStoreValue(ScalarValue value) {
            return value != null;
        }
    };

    public static final Operator TAIL = new Operator("tail");

    public Operator(String name) {
    	//操作符名称
        this.name = name;
        OPERATOR_NAME_MAP.put(name, this);
    }

    public static Operator getOperator(String name) {
        if (!OPERATOR_NAME_MAP.containsKey(name))
            throw new IllegalArgumentException("The operator \"" + name + "\" does not exist.");
        return (Operator) OPERATOR_NAME_MAP.get(name);
    }
    
    //得到对应操作符编解码过程
    public OperatorCodec getCodec(Type type) {
        return OperatorCodec.getCodec(this, type);
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
    
    //是否需要将值存放在字典中
    public boolean shouldStoreValue(ScalarValue value) {
        return true;
    }

    public void validate(Scalar scalar) {
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || !(other instanceof Operator))
            return false;
        return equals((Operator) other);
    }

    private boolean equals(Operator other) {
        return name.equals(other.name);
    }

    public int hashCode() {
        return name.hashCode();
    }
    
    //是否使用字典
    public boolean usesDictionary() {
        return true;
    }
}
